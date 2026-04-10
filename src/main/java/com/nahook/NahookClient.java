package com.nahook;

import com.nahook.types.*;

import java.time.Duration;
import java.util.*;

/**
 * Client for the Nahook ingestion API.
 */
public final class NahookClient {

    private final HttpClientWrapper http;

    /**
     * Create a client with default options.
     *
     * @param apiKey API key starting with {@code nhk_}
     * @throws IllegalArgumentException if the key prefix is invalid
     */
    public NahookClient(String apiKey) {
        this(apiKey, null);
    }

    /**
     * Create a client with custom options.
     */
    public NahookClient(String apiKey, ClientOptions options) {
        if (apiKey == null || !apiKey.startsWith("nhk_")) {
            throw new IllegalArgumentException("Invalid API key: must start with 'nhk_'");
        }
        this.http = new HttpClientWrapper(
                apiKey,
                options != null ? options.getBaseUrl() : null,
                options != null ? options.getTimeout() : null,
                options != null ? options.getRetries() : null
        );
    }

    private NahookClient(Builder builder) {
        if (builder.apiKey == null || !builder.apiKey.startsWith("nhk_")) {
            throw new IllegalArgumentException("Invalid API key: must start with 'nhk_'");
        }
        this.http = new HttpClientWrapper(builder.apiKey, builder.baseUrl,
                builder.timeout, builder.retries);
    }

    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    /**
     * Send a payload to a specific endpoint.
     * Auto-generates a UUID idempotency key if not provided.
     */
    public SendResult send(String endpointId, SendOptions options) {
        String idempotencyKey = options.getIdempotencyKey() != null
                ? options.getIdempotencyKey() : UUID.randomUUID().toString();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("payload", options.getPayload());
        body.put("idempotencyKey", idempotencyKey);

        String path = "/api/ingest/" + HttpClientWrapper.encodePath(endpointId);
        return http.request("POST", path, body, SendResult.class);
    }

    /**
     * Fan-out a payload by event type to all subscribed endpoints.
     */
    public TriggerResult trigger(String eventType, TriggerOptions options) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("payload", options.getPayload());
        if (options.getMetadata() != null) {
            body.put("metadata", options.getMetadata());
        }

        String path = "/api/ingest/event/" + HttpClientWrapper.encodePath(eventType);
        return http.request("POST", path, body, TriggerResult.class);
    }

    /**
     * Batch send to multiple specific endpoints (max 20 items).
     */
    public BatchResult sendBatch(List<SendBatchItem> items) {
        Map<String, Object> body = Collections.singletonMap("items", items);
        return http.request("POST", "/api/ingest/batch", body, BatchResult.class);
    }

    /**
     * Batch fan-out by event types (max 20 items).
     */
    public BatchResult triggerBatch(List<TriggerBatchItem> items) {
        Map<String, Object> body = Collections.singletonMap("items", items);
        return http.request("POST", "/api/ingest/event/batch", body, BatchResult.class);
    }

    public static class Builder {
        private final String apiKey;
        private String baseUrl;
        private Duration timeout;
        private Integer retries;

        private Builder(String apiKey) { this.apiKey = apiKey; }

        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder timeout(Duration timeout) { this.timeout = timeout; return this; }
        public Builder retries(int retries) { this.retries = retries; return this; }
        public NahookClient build() { return new NahookClient(this); }
    }
}
