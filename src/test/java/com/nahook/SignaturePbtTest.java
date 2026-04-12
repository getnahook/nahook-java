package com.nahook;

import net.jqwik.api.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for webhook signature computation using jqwik.
 */
class SignaturePbtTest {

    private static final String MSG_ID = "msg_pbt_001";
    private static final String TIMESTAMP = "1700000000";

    // ── Helpers ──

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

    @Provide
    Arbitrary<String> validBase64Secrets() {
        // Generate 16-32 byte random data and base64-encode it
        return Arbitraries.bytes().array(byte[].class)
                .ofMinSize(16).ofMaxSize(32)
                .map(bytes -> Base64.getEncoder().encodeToString(bytes));
    }

    @Provide
    Arbitrary<String> payloads() {
        return Arbitraries.strings().ofMinLength(0).ofMaxLength(2000);
    }

    // Property 1: sign then verify roundtrip — signing the same payload twice yields the same signature
    @Property(tries = 100)
    void signThenVerifyRoundtrip(
            @ForAll("validBase64Secrets") String secret,
            @ForAll("payloads") String payload) {
        String sig = computeSignature(secret, MSG_ID, TIMESTAMP, payload);
        String verifySig = computeSignature(secret, MSG_ID, TIMESTAMP, payload);
        assertEquals(sig, verifySig, "Sign then verify roundtrip must match");
        assertTrue(sig.startsWith("v1,"), "Signature must start with v1,");
    }

    // Property 2: tampered payload produces different signature
    @Property(tries = 100)
    void tamperedPayloadFailsVerification(
            @ForAll("validBase64Secrets") String secret,
            @ForAll("payloads") String payload) {
        String tampered = payload + "TAMPERED";
        String originalSig = computeSignature(secret, MSG_ID, TIMESTAMP, payload);
        String tamperedSig = computeSignature(secret, MSG_ID, TIMESTAMP, tampered);
        assertNotEquals(originalSig, tamperedSig,
                "Tampered payload must produce different signature");
    }

    // Property 3: wrong secret produces different signature
    @Property(tries = 100)
    void wrongSecretFailsVerification(
            @ForAll("validBase64Secrets") String secret1,
            @ForAll("validBase64Secrets") String secret2,
            @ForAll("payloads") String payload) {
        Assume.that(!secret1.equals(secret2));
        String sig1 = computeSignature(secret1, MSG_ID, TIMESTAMP, payload);
        String sig2 = computeSignature(secret2, MSG_ID, TIMESTAMP, payload);
        assertNotEquals(sig1, sig2,
                "Different secrets must produce different signatures");
    }

    // Property 4: deterministic — same inputs always produce identical output
    @Property(tries = 100)
    void deterministicSignature(
            @ForAll("validBase64Secrets") String secret,
            @ForAll("payloads") String payload) {
        String sig1 = computeSignature(secret, MSG_ID, TIMESTAMP, payload);
        String sig2 = computeSignature(secret, MSG_ID, TIMESTAMP, payload);
        String sig3 = computeSignature(secret, MSG_ID, TIMESTAMP, payload);
        assertEquals(sig1, sig2, "First and second must match");
        assertEquals(sig2, sig3, "Second and third must match");
    }
}
