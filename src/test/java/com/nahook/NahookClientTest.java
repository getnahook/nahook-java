package com.nahook;

import com.nahook.errors.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NahookClientTest {

    // ---- NahookClient API key validation ----

    @Test
    void testRejectsInvalidApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new NahookClient("bad_key"));
        assertThrows(IllegalArgumentException.class, () -> new NahookClient(""));
        assertThrows(IllegalArgumentException.class, () -> new NahookClient(null));
        assertThrows(IllegalArgumentException.class, () -> new NahookClient("nhm_wrongprefix"));
    }

    @Test
    void testAcceptsValidApiKey() {
        assertDoesNotThrow(() -> new NahookClient("nhk_validkey123"));
    }

    @Test
    void testBuilderRejectsInvalidApiKey() {
        assertThrows(IllegalArgumentException.class,
                () -> NahookClient.builder("bad_key").build());
    }

    @Test
    void testBuilderAcceptsValidApiKey() {
        assertDoesNotThrow(() -> NahookClient.builder("nhk_test").build());
    }

    // ---- NahookManagement token validation ----

    @Test
    void testRejectsInvalidManagementToken() {
        assertThrows(IllegalArgumentException.class, () -> new NahookManagement("bad_token"));
        assertThrows(IllegalArgumentException.class, () -> new NahookManagement(""));
        assertThrows(IllegalArgumentException.class, () -> new NahookManagement(null));
        assertThrows(IllegalArgumentException.class, () -> new NahookManagement("nhk_wrongprefix"));
    }

    @Test
    void testAcceptsValidManagementToken() {
        assertDoesNotThrow(() -> new NahookManagement("nhm_validtoken123"));
    }

    @Test
    void testManagementBuilderRejectsInvalidToken() {
        assertThrows(IllegalArgumentException.class,
                () -> NahookManagement.builder("bad").build());
    }

    @Test
    void testManagementBuilderAcceptsValidToken() {
        assertDoesNotThrow(() -> NahookManagement.builder("nhm_test").build());
    }

    // ---- Management Builder has no retries option ----

    @Test
    void testManagementHasNoRetriesOption() {
        Method[] methods = NahookManagement.Builder.class.getDeclaredMethods();
        boolean hasRetries = Arrays.stream(methods)
                .anyMatch(m -> m.getName().equals("retries"));
        assertFalse(hasRetries,
                "NahookManagement.Builder should not expose a retries() method");
    }

    // ---- Management resource accessors ----

    @Test
    void testManagementHasEnvironmentsResource() {
        NahookManagement mgmt = new NahookManagement("nhm_test123");
        assertNotNull(mgmt.environments(), "environments() should not be null");
    }

    @Test
    void testEnvironmentsResourceHasAllMethods() throws Exception {
        Class<?> clazz = com.nahook.resources.EnvironmentsResource.class;

        assertNotNull(clazz.getMethod("list", String.class));
        assertNotNull(clazz.getMethod("create", String.class, com.nahook.types.CreateEnvironmentOptions.class));
        assertNotNull(clazz.getMethod("get", String.class, String.class));
        assertNotNull(clazz.getMethod("update", String.class, String.class, com.nahook.types.UpdateEnvironmentOptions.class));
        assertNotNull(clazz.getMethod("delete", String.class, String.class));
        assertNotNull(clazz.getMethod("listEventTypeVisibility", String.class, String.class));
        assertNotNull(clazz.getMethod("setEventTypeVisibility", String.class, String.class, String.class, com.nahook.types.SetVisibilityOptions.class));
    }

    // ---- Exception hierarchy ----

    @Test
    void testExceptionHierarchy() {
        // NahookException extends RuntimeException
        assertTrue(RuntimeException.class.isAssignableFrom(NahookException.class));

        // NahookApiException extends NahookException
        assertTrue(NahookException.class.isAssignableFrom(NahookApiException.class));

        // NahookTimeoutException extends NahookException
        assertTrue(NahookException.class.isAssignableFrom(NahookTimeoutException.class));

        // NahookNetworkException extends NahookException
        assertTrue(NahookException.class.isAssignableFrom(NahookNetworkException.class));
    }

    // ---- NahookApiException properties ----

    @Test
    void testApiExceptionProperties() {
        NahookApiException ex = new NahookApiException(500, "internal_error", "Server error", null);
        assertEquals(500, ex.getStatus());
        assertEquals("internal_error", ex.getCode());
        assertEquals("Server error", ex.getMessage());
        assertNull(ex.getRetryAfter());
    }

    @Test
    void testApiExceptionIsRetryableOn500() {
        NahookApiException ex = new NahookApiException(500, "internal_error", "fail", null);
        assertTrue(ex.isRetryable());
        assertFalse(ex.isAuthError());
        assertFalse(ex.isNotFound());
        assertFalse(ex.isRateLimited());
        assertFalse(ex.isValidationError());
    }

    @Test
    void testApiExceptionIsRetryableOn502() {
        NahookApiException ex = new NahookApiException(502, "bad_gateway", "fail", null);
        assertTrue(ex.isRetryable());
    }

    @Test
    void testApiExceptionIsRetryableOn429() {
        NahookApiException ex = new NahookApiException(429, "rate_limited", "slow down", 60);
        assertTrue(ex.isRetryable());
        assertTrue(ex.isRateLimited());
        assertEquals(60, ex.getRetryAfter());
    }

    @Test
    void testApiExceptionIsAuthErrorOn401() {
        NahookApiException ex = new NahookApiException(401, "unauthorized", "bad token", null);
        assertTrue(ex.isAuthError());
        assertFalse(ex.isRetryable());
    }

    @Test
    void testApiExceptionIsAuthErrorOn403WithTokenDisabled() {
        NahookApiException ex = new NahookApiException(403, "token_disabled", "disabled", null);
        assertTrue(ex.isAuthError());
    }

    @Test
    void testApiExceptionIsNotAuthErrorOn403WithOtherCode() {
        NahookApiException ex = new NahookApiException(403, "forbidden", "nope", null);
        assertFalse(ex.isAuthError());
    }

    @Test
    void testApiExceptionIsNotFoundOn404() {
        NahookApiException ex = new NahookApiException(404, "not_found", "missing", null);
        assertTrue(ex.isNotFound());
        assertFalse(ex.isRetryable());
    }

    @Test
    void testApiExceptionIsValidationErrorOn400() {
        NahookApiException ex = new NahookApiException(400, "validation_error", "bad input", null);
        assertTrue(ex.isValidationError());
        assertFalse(ex.isRetryable());
    }

    @Test
    void testApiExceptionNonRetryableOn404() {
        NahookApiException ex = new NahookApiException(404, "not_found", "missing", null);
        assertFalse(ex.isRetryable());
    }

    // ---- NahookTimeoutException ----

    @Test
    void testTimeoutExceptionChainesCause() {
        IOException cause = new IOException("connection timed out");
        NahookTimeoutException ex = new NahookTimeoutException(5000, cause);

        assertSame(cause, ex.getCause());
        assertEquals(5000, ex.getTimeoutMs());
        assertTrue(ex.getMessage().contains("5000"));
    }

    // ---- NahookNetworkException ----

    @Test
    void testNetworkExceptionChainesCause() {
        IOException cause = new IOException("connection refused");
        NahookNetworkException ex = new NahookNetworkException(cause);

        assertSame(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("connection refused"));
    }

    // ---- Management exposes resource accessors ----

    @Test
    void testManagementResourceAccessors() {
        NahookManagement mgmt = new NahookManagement("nhm_test");
        assertNotNull(mgmt.endpoints());
        assertNotNull(mgmt.eventTypes());
        assertNotNull(mgmt.applications());
        assertNotNull(mgmt.subscriptions());
        assertNotNull(mgmt.portalSessions());
    }
}
