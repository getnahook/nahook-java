package com.nahook;

import com.nahook.errors.NahookApiException;
import com.nahook.errors.NahookException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Negative / resilience tests using MockWebServer.
 * Verifies the SDK handles malformed, empty, and unexpected responses gracefully.
 */
class NegativeTest {

    private MockWebServer server;
    private NahookManagement mgmt;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        mgmt = NahookManagement.builder("nhm_test123")
                .baseUrl(server.url("/").toString())
                .build();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    // NEG-01: malformed JSON response on 200 throws exception (not crash)
    @Test
    void neg01MalformedJsonResponseThrowsException() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{invalid json!!!"));

        assertThrows(NahookException.class, () -> mgmt.endpoints().list("ws_123"));
    }

    // NEG-02: empty body 200 throws meaningful error
    @Test
    void neg02EmptyBody200ThrowsMeaningfulError() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(""));

        assertThrows(NahookException.class, () -> mgmt.endpoints().list("ws_123"));
    }

    // NEG-03: 5xx with HTML body throws NahookApiException with status
    @Test
    void neg03HtmlErrorResponseThrowsApiException() {
        server.enqueue(new MockResponse()
                .setResponseCode(503)
                .setHeader("Content-Type", "text/html")
                .setBody("<html><body>Service Unavailable</body></html>"));

        NahookApiException ex = assertThrows(NahookApiException.class,
                () -> mgmt.endpoints().list("ws_123"));
        assertEquals(503, ex.getStatus());
        assertTrue(ex.isRetryable());
    }

    // NEG-04: empty body 500 throws NahookApiException
    @Test
    void neg04EmptyBody500ThrowsApiException() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody(""));

        NahookApiException ex = assertThrows(NahookApiException.class,
                () -> mgmt.endpoints().list("ws_123"));
        assertEquals(500, ex.getStatus());
        assertTrue(ex.isRetryable());
    }

    // NEG-05: unknown fields in response succeed without error
    @Test
    void neg05UnknownFieldsSucceedWithoutError() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"id\":\"ep_1\",\"url\":\"https://example.com\",\"isActive\":true,\"type\":\"webhook\",\"unknownField\":\"should_be_ignored\",\"nested\":{\"deep\":true}}]"));

        var result = assertDoesNotThrow(() -> mgmt.endpoints().list("ws_123"));
        assertNotNull(result);
        assertFalse(result.getData().isEmpty());
        assertEquals("ep_1", result.getData().get(0).getId());
    }

    // NEG-06: missing optional fields succeed
    @Test
    void neg06MissingFieldsSucceedGracefully() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"id\":\"ep_1\"}]"));

        var result = assertDoesNotThrow(() -> mgmt.endpoints().list("ws_123"));
        assertNotNull(result);
        assertFalse(result.getData().isEmpty());
        assertEquals("ep_1", result.getData().get(0).getId());
    }
}
