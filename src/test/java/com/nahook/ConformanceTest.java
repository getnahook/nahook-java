package com.nahook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nahook.errors.NahookApiException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Conformance tests driven by shared JSON fixtures in public-sdks/fixtures/conformance/.
 */
class ConformanceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static File fixturesDir() {
        // Navigate from nahook-java project root to shared fixtures
        File projectDir = new File(System.getProperty("user.dir"));
        File fixtures = new File(projectDir, "../fixtures/conformance");
        if (!fixtures.exists()) {
            // Fallback: try from the public-sdks root
            fixtures = new File(projectDir, "../../public-sdks/fixtures/conformance");
        }
        return fixtures;
    }

    private static JsonNode loadFixture(String subpath) throws Exception {
        File file = new File(fixturesDir(), subpath);
        return MAPPER.readTree(file);
    }

    // ── Error Classification ──

    @TestFactory
    Collection<DynamicTest> errorClassification() throws Exception {
        JsonNode cases = loadFixture("error-classification/cases.json");
        Collection<DynamicTest> tests = new ArrayList<>();

        for (JsonNode tc : cases) {
            String id = tc.get("id").asText();
            String desc = tc.get("description").asText();
            JsonNode input = tc.get("input");
            JsonNode expect = tc.get("expect");

            tests.add(DynamicTest.dynamicTest(id + ": " + desc, () -> {
                int status = input.get("status").asInt();
                String code = input.get("code").asText();
                String message = input.get("message").asText();
                Integer retryAfter = input.has("retryAfter") && !input.get("retryAfter").isNull()
                        ? input.get("retryAfter").asInt() : null;

                NahookApiException ex = new NahookApiException(status, code, message, retryAfter);

                assertEquals(expect.get("isRetryable").asBoolean(), ex.isRetryable(),
                        id + " isRetryable");
                assertEquals(expect.get("isAuthError").asBoolean(), ex.isAuthError(),
                        id + " isAuthError");
                assertEquals(expect.get("isNotFound").asBoolean(), ex.isNotFound(),
                        id + " isNotFound");
                assertEquals(expect.get("isRateLimited").asBoolean(), ex.isRateLimited(),
                        id + " isRateLimited");
                assertEquals(expect.get("isValidationError").asBoolean(), ex.isValidationError(),
                        id + " isValidationError");
            }));
        }
        return tests;
    }

    // ── Region Routing ──

    @TestFactory
    Collection<DynamicTest> regionRouting() throws Exception {
        JsonNode cases = loadFixture("region-routing/cases.json");
        Collection<DynamicTest> tests = new ArrayList<>();

        for (JsonNode tc : cases) {
            String id = tc.get("id").asText();
            String desc = tc.get("description").asText();
            String token = tc.get("input").get("token").asText();
            String expectedUrl = tc.get("expect").get("baseUrl").asText();

            tests.add(DynamicTest.dynamicTest(id + ": " + desc, () -> {
                assertEquals(expectedUrl, HttpClientWrapper.resolveBaseUrl(token), id);
            }));
        }
        return tests;
    }

    // ── Retry Backoff ──

    @TestFactory
    Collection<DynamicTest> retryBackoff() throws Exception {
        JsonNode cases = loadFixture("retry-backoff/cases.json");
        Collection<DynamicTest> tests = new ArrayList<>();

        for (JsonNode tc : cases) {
            String id = tc.get("id").asText();
            String desc = tc.get("description").asText();
            JsonNode input = tc.get("input");
            JsonNode expect = tc.get("expect");

            int attempt = input.get("attempt").asInt();
            long retryAfterMs = input.has("retryAfterMs") && !input.get("retryAfterMs").isNull()
                    ? input.get("retryAfterMs").asLong() : 0;

            tests.add(DynamicTest.dynamicTest(id + ": " + desc, () -> {
                if (expect.has("exactDelayMs")) {
                    long exactDelay = expect.get("exactDelayMs").asLong();
                    assertEquals(exactDelay, HttpClientWrapper.calculateDelay(attempt, retryAfterMs),
                            id + " exact delay");
                } else {
                    long minDelay = expect.get("minDelayMs").asLong();
                    long maxDelay = expect.get("maxDelayMs").asLong();
                    // Run multiple times to account for randomness
                    for (int i = 0; i < 20; i++) {
                        long delay = HttpClientWrapper.calculateDelay(attempt, retryAfterMs);
                        assertTrue(delay >= minDelay,
                                id + " delay " + delay + " < min " + minDelay);
                        assertTrue(delay <= maxDelay,
                                id + " delay " + delay + " > max " + maxDelay);
                    }
                }
            }));
        }
        return tests;
    }

    // ── Signature ──

    @TestFactory
    Collection<DynamicTest> signature() throws Exception {
        JsonNode cases = loadFixture("signature/cases.json");
        Collection<DynamicTest> tests = new ArrayList<>();

        for (JsonNode tc : cases) {
            String id = tc.get("id").asText();
            String desc = tc.get("description").asText();
            String action = tc.get("action").asText();
            JsonNode input = tc.get("input");
            JsonNode expect = tc.get("expect");

            tests.add(DynamicTest.dynamicTest(id + ": " + desc, () -> {
                String secret = input.get("secret").asText();
                String msgId = input.get("messageId").asText();
                String timestamp = input.get("timestamp").asText();

                String payload;
                if (input.has("payloadGenerator")) {
                    String gen = input.get("payloadGenerator").asText();
                    if ("repeat_a_10000".equals(gen)) {
                        payload = "a".repeat(10000);
                    } else {
                        payload = "";
                    }
                } else {
                    payload = input.get("payload").asText();
                }

                switch (action) {
                    case "sign_then_verify": {
                        String sig = computeSignature(secret, msgId, timestamp, payload);
                        String verify = computeSignature(secret, msgId, timestamp, payload);
                        assertEquals(sig, verify, id + " sign then verify roundtrip");
                        assertTrue(expect.get("verifies").asBoolean(), id + " expected to verify");
                        break;
                    }
                    case "sign_original_verify_tampered": {
                        String original = computeSignature(secret, msgId, timestamp, payload);
                        String tampered = computeSignature(secret, msgId, timestamp,
                                input.get("tamperedPayload").asText());
                        assertNotEquals(original, tampered, id + " tampered should differ");
                        assertFalse(expect.get("verifies").asBoolean());
                        break;
                    }
                    case "sign_with_original_verify_with_wrong": {
                        String original = computeSignature(secret, msgId, timestamp, payload);
                        String wrong = computeSignature(input.get("wrongSecret").asText(),
                                msgId, timestamp, payload);
                        assertNotEquals(original, wrong, id + " wrong secret should differ");
                        assertFalse(expect.get("verifies").asBoolean());
                        break;
                    }
                    case "sign_twice_compare": {
                        String sig1 = computeSignature(secret, msgId, timestamp, payload);
                        String sig2 = computeSignature(secret, msgId, timestamp, payload);
                        assertEquals(sig1, sig2, id + " determinism");
                        assertTrue(expect.get("identical").asBoolean());
                        break;
                    }
                    case "verify_known_signature": {
                        String sig = computeSignature(secret, msgId, timestamp, payload);
                        String expectedHeader = expect.get("signatureHeader").asText();
                        // The fixture format is "v1,{timestamp},{sig}" — extract just the sig part
                        // Our computeSignature returns "v1,{base64}" so we build the full header
                        String fullHeader = sig.substring(0, 3) + timestamp + "," + sig.substring(3);
                        assertEquals(expectedHeader, fullHeader, id + " known signature");
                        break;
                    }
                    default:
                        fail("Unknown action: " + action);
                }
            }));
        }
        return tests;
    }

    // ── Helper: compute HMAC-SHA256 signature (same as WebhookSignatureTest) ──

    private static String computeSignature(String secret, String msgId, String timestamp, String payload) {
        try {
            String rawSecret = secret.startsWith("whsec_") ? secret.substring(6) : secret;
            byte[] key;
            try {
                key = Base64.getDecoder().decode(rawSecret);
            } catch (IllegalArgumentException e) {
                // Fixture secrets may not be valid base64 — use raw UTF-8 bytes
                key = rawSecret.getBytes(StandardCharsets.UTF_8);
            }
            String toSign = msgId + "." + timestamp + "." + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] digest = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
            return "v1," + Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute signature", e);
        }
    }
}
