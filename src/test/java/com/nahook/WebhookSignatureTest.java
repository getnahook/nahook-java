package com.nahook;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Webhook signature verification tests.
 *
 * Validates that the Standard Webhooks signing format used by the Nahook API
 * can be correctly produced and verified using native crypto.
 *
 * Signing spec:
 *   base   = "{msgId}.{timestamp}.{payload}"
 *   key    = base64_decode(secret_without_whsec_prefix)
 *   sig    = "v1," + base64(HMAC-SHA256(key, base))
 *   headers: webhook-id, webhook-timestamp, webhook-signature
 */
class WebhookSignatureTest {

    private static final String TEST_SECRET = "whsec_dGVzdF93ZWJob29rX3NpZ25pbmdfa2V5XzMyYnl0ZXMh";
    private static final String MSG_ID = "msg_test_sig_001";
    private static final String TIMESTAMP = "1712345678";
    private static final String PAYLOAD = "{\"order_id\":\"ord_123\",\"amount\":49.99}";

    private static String computeSignature(String secret, String msgId, String timestamp, String payload) {
        try {
            String rawSecret = secret.startsWith("whsec_") ? secret.substring(6) : secret;
            byte[] key = Base64.getDecoder().decode(rawSecret);

            String toSign = msgId + "." + timestamp + "." + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] digest = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));

            return "v1," + Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute signature", e);
        }
    }

    @Test
    void producesValidV1Signature() {
        String sig = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        assertTrue(sig.matches("^v1,[A-Za-z0-9+/]+=*$"),
                "signature should match v1 format, got: " + sig);
    }

    @Test
    void deterministicSameInputsSameSignature() {
        String sig1 = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        String sig2 = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        assertEquals(sig1, sig2);
    }

    @Test
    void rejectsTamperedPayload() {
        String original = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        String tampered = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP,
                "{\"order_id\":\"ord_123\",\"amount\":99.99}");
        assertNotEquals(original, tampered);
    }

    @Test
    void rejectsWrongSecret() {
        String original = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        String wrong = computeSignature("whsec_d3Jvbmdfc2VjcmV0", MSG_ID, TIMESTAMP, PAYLOAD);
        assertNotEquals(original, wrong);
    }

    @Test
    void rejectsTamperedMsgId() {
        String original = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        String tampered = computeSignature(TEST_SECRET, "msg_tampered_id", TIMESTAMP, PAYLOAD);
        assertNotEquals(original, tampered);
    }

    @Test
    void rejectsTamperedTimestamp() {
        String original = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        String tampered = computeSignature(TEST_SECRET, MSG_ID, "9999999999", PAYLOAD);
        assertNotEquals(original, tampered);
    }

    @Test
    void correctHeadersStructure() {
        String sig = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        Map<String, String> headers = Map.of(
                "content-type", "application/json",
                "webhook-id", MSG_ID,
                "webhook-timestamp", TIMESTAMP,
                "webhook-signature", sig
        );

        assertTrue(headers.get("webhook-id").startsWith("msg_"));
        assertTrue(headers.get("webhook-signature").startsWith("v1,"));
        assertTrue(headers.get("webhook-timestamp").matches("\\d+"));
        assertEquals("application/json", headers.get("content-type"));
    }

    @Test
    void handlesSecretWithoutPrefix() {
        String rawSecret = TEST_SECRET.substring(6);
        String withPrefix = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        String withoutPrefix = computeSignature(rawSecret, MSG_ID, TIMESTAMP, PAYLOAD);
        assertEquals(withPrefix, withoutPrefix);
    }

    @Test
    void matchesKnownCrossLanguageReferenceSignature() {
        String sig = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, PAYLOAD);
        assertEquals("v1,VF1JBS4kdSwmE64FeeiWTgszlPCfaop53x8bwzvHizw=", sig);
    }

    @Test
    void emptyPayloadProducesValidSignature() {
        String sig = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP, "");
        assertEquals("v1,yNFeVvBSs4aZ/sVHHw1MaUWnN1IGK/Ul/16T8aptSJo=", sig);
    }

    @Test
    void unicodePayloadConsistentAcrossLanguages() {
        String sig = computeSignature(TEST_SECRET, MSG_ID, TIMESTAMP,
                "{\"name\":\"caf\u00e9\",\"price\":\"\u20ac9.99\"}");
        assertEquals("v1,GcuGAMV9tELnF2rjay6sA8uo5PDPPlhaFi6gKUg06wQ=", sig);
    }
}
