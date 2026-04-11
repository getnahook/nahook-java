package com.nahook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nahook.errors.NahookApiException;
import com.nahook.errors.NahookNetworkException;
import com.nahook.errors.NahookTimeoutException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Internal HTTP layer with retry logic.
 */
public class HttpClientWrapper {

    private static final String DEFAULT_BASE_URL = "https://api.nahook.com";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final String SDK_VERSION = "0.1.0";
    private static final String USER_AGENT = "nahook-java/" + SDK_VERSION;
    private static final long BASE_DELAY_MS = 500;
    private static final long MAX_DELAY_MS = 10_000;

    private static final Map<String, String> REGION_BASE_URLS = Map.of(
            "us", "https://us.api.nahook.com",
            "eu", "https://eu.api.nahook.com",
            "ap", "https://ap.api.nahook.com"
    );

    /**
     * Extract region slug from an nhk_ API key and resolve its base URL.
     */
    static String resolveBaseUrl(String token) {
        if (token != null && token.length() >= 7 && token.startsWith("nhk_") && token.charAt(6) == '_') {
            String slug = token.substring(4, 6);
            String url = REGION_BASE_URLS.get(slug);
            if (url != null) return url;
        }
        return DEFAULT_BASE_URL;
    }

    static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String token;
    private final String baseUrl;
    private final Duration timeout;
    private final int retries;
    private final HttpClient httpClient;

    public HttpClientWrapper(String token, String baseUrl, Duration timeout, Integer retries) {
        this.token = token;
        this.baseUrl = baseUrl != null ? baseUrl.replaceAll("/+$", "") : resolveBaseUrl(token);
        this.timeout = timeout != null ? timeout : DEFAULT_TIMEOUT;
        this.retries = retries != null ? retries : 0;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    public <T> T request(String method, String path, Object body, Class<T> responseType) {
        return request(method, path, body, null, responseType);
    }

    public <T> T request(String method, String path, Object body, Map<String, Object> query, Class<T> responseType) {
        HttpResponse<String> response = executeWithRetry(method, path, body, query);
        if (response.statusCode() == 204) {
            return null;
        }
        try {
            return MAPPER.readValue(response.body(), responseType);
        } catch (JsonProcessingException e) {
            throw new NahookNetworkException(e);
        }
    }

    public String requestRaw(String method, String path, Object body) {
        HttpResponse<String> response = executeWithRetry(method, path, body, null);
        return response.body();
    }

    private HttpResponse<String> executeWithRetry(String method, String path, Object body, Map<String, Object> query) {
        String url = buildUrl(path, query);
        Object lastError = null;

        for (int attempt = 0; attempt <= retries; attempt++) {
            if (attempt > 0) {
                long retryAfterMs = 0;
                if (lastError instanceof NahookApiException) {
                    Integer ra = ((NahookApiException) lastError).getRetryAfter();
                    retryAfterMs = ra != null ? ra * 1000L : 0;
                }
                long delay = calculateDelay(attempt - 1, retryAfterMs);
                try { Thread.sleep(delay); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new NahookNetworkException(e);
                }
            }

            try {
                HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(timeout)
                        .header("Authorization", "Bearer " + token)
                        .header("Accept", "application/json")
                        .header("User-Agent", USER_AGENT);

                if (body != null) {
                    String json = MAPPER.writeValueAsString(body);
                    reqBuilder.header("Content-Type", "application/json")
                            .method(method, HttpRequest.BodyPublishers.ofString(json));
                } else {
                    reqBuilder.method(method, HttpRequest.BodyPublishers.noBody());
                }

                HttpResponse<String> response = httpClient.send(reqBuilder.build(),
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return response;
                }

                NahookApiException apiError = parseError(response);
                if (attempt < retries && apiError.isRetryable()) {
                    lastError = apiError;
                    continue;
                }
                throw apiError;

            } catch (java.net.http.HttpTimeoutException e) {
                NahookTimeoutException te = new NahookTimeoutException(timeout.toMillis(), e);
                if (attempt < retries) { lastError = te; continue; }
                throw te;
            } catch (IOException e) {
                NahookNetworkException ne = new NahookNetworkException(e);
                if (attempt < retries) { lastError = ne; continue; }
                throw ne;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new NahookNetworkException(e);
            } catch (NahookApiException e) {
                throw e;
            } catch (RuntimeException e) {
                throw e;
            }
        }

        if (lastError instanceof RuntimeException) throw (RuntimeException) lastError;
        throw new NahookNetworkException(new RuntimeException("Request failed after retries"));
    }

    private NahookApiException parseError(HttpResponse<String> response) {
        Integer retryAfter = null;
        String raHeader = response.headers().firstValue("retry-after").orElse(null);
        if (raHeader != null) {
            try { retryAfter = Integer.parseInt(raHeader); } catch (NumberFormatException ignored) {}
        }

        try {
            JsonNode root = MAPPER.readTree(response.body());
            JsonNode errorNode = root.path("error");
            String code = errorNode.path("code").asText("unknown");
            String message = errorNode.path("message").asText(String.valueOf(response.statusCode()));
            return new NahookApiException(response.statusCode(), code, message, retryAfter);
        } catch (Exception e) {
            return new NahookApiException(response.statusCode(), "unknown",
                    String.valueOf(response.statusCode()), retryAfter);
        }
    }

    private String buildUrl(String path, Map<String, Object> query) {
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(path);
        if (query != null && !query.isEmpty()) {
            sb.append('?');
            boolean first = true;
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                if (entry.getValue() == null) continue;
                if (!first) sb.append('&');
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                sb.append('=');
                sb.append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8));
                first = false;
            }
        }
        return sb.toString();
    }

    public static String encodePath(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }

    static long calculateDelay(int attempt, long retryAfterMs) {
        if (retryAfterMs > 0) return retryAfterMs;
        long exponential = Math.min(MAX_DELAY_MS, BASE_DELAY_MS * (1L << attempt));
        return (long) (exponential * ThreadLocalRandom.current().nextDouble());
    }
}
