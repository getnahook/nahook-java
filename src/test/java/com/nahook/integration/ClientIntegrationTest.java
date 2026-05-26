package com.nahook.integration;

import com.nahook.NahookClient;
import com.nahook.errors.NahookApiException;
import com.nahook.types.*;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests that hit a real Nahook API instance.
 * Skipped automatically when NAHOOK_TEST_* env vars are not set.
 */
@Tag("integration")
class ClientIntegrationTest {

    private static String apiUrl;
    private static String apiKey;
    private static String disabledApiKey;
    private static String endpointId;
    private static String eventType;

    private static NahookClient client;

    @BeforeAll
    static void setUp() {
        apiUrl = System.getenv("NAHOOK_TEST_API_URL");
        apiKey = System.getenv("NAHOOK_TEST_API_KEY");
        disabledApiKey = System.getenv("NAHOOK_TEST_DISABLED_API_KEY");
        endpointId = System.getenv("NAHOOK_TEST_ENDPOINT_ID");
        eventType = System.getenv("NAHOOK_TEST_EVENT_TYPE");

        assumeTrue(apiUrl != null && !apiUrl.isEmpty(), "NAHOOK_TEST_API_URL not set");
        assumeTrue(apiKey != null && !apiKey.isEmpty(), "NAHOOK_TEST_API_KEY not set");
        assumeTrue(disabledApiKey != null && !disabledApiKey.isEmpty(), "NAHOOK_TEST_DISABLED_API_KEY not set");
        assumeTrue(endpointId != null && !endpointId.isEmpty(), "NAHOOK_TEST_ENDPOINT_ID not set");
        assumeTrue(eventType != null && !eventType.isEmpty(), "NAHOOK_TEST_EVENT_TYPE not set");

        ClientOptions options = ClientOptions.builder()
                .baseUrl(apiUrl)
                .retries(0)
                .build();
        client = new NahookClient(apiKey, options);
    }

    // ---------------------------------------------------------------
    // send
    // ---------------------------------------------------------------

    @Test
    void send_happyPath_returnsAcceptedWithDeliveryId() {
        Map<String, Object> payload = Map.of("order_id", "ord_1001", "amount", 49.99);
        SendOptions opts = new SendOptions(payload);

        SendResult result = client.send(endpointId, opts);

        assertEquals("accepted", result.getStatus());
        assertNotNull(result.getDeliveryId());
        assertTrue(result.getDeliveryId().startsWith("del_"),
                "deliveryId should start with del_, got: " + result.getDeliveryId());
    }

    @Test
    void send_idempotencyDedup_returnsSameDeliveryId() {
        String idempotencyKey = "idem-java-dedup-" + UUID.randomUUID();
        Map<String, Object> payload = Map.of("order_id", "ord_1002");

        SendOptions opts = new SendOptions(payload, idempotencyKey);

        SendResult first = client.send(endpointId, opts);
        SendResult second = client.send(endpointId, opts);

        assertEquals(first.getDeliveryId(), second.getDeliveryId(),
                "same idempotency key should return the same deliveryId");
    }

    @Test
    void send_separateKeys_returnsDifferentDeliveryIds() {
        Map<String, Object> payload = Map.of("order_id", "ord_1003");

        SendOptions opts1 = new SendOptions(payload, "idem-java-a-" + UUID.randomUUID());
        SendOptions opts2 = new SendOptions(payload, "idem-java-b-" + UUID.randomUUID());

        SendResult first = client.send(endpointId, opts1);
        SendResult second = client.send(endpointId, opts2);

        assertNotEquals(first.getDeliveryId(), second.getDeliveryId(),
                "different idempotency keys should produce different deliveryIds");
    }

    // ---------------------------------------------------------------
    // trigger
    // ---------------------------------------------------------------

    @Test
    void trigger_fanOut_returnsAcceptedWithEventTypeId() {
        Map<String, Object> payload = Map.of("order_id", "ord_2001", "action", "created");
        TriggerOptions opts = new TriggerOptions(payload);

        TriggerResult result = client.trigger(eventType, opts);

        assertEquals("accepted", result.getStatus());
        assertNotNull(result.getEventTypeId());
        assertTrue(result.getEventTypeId().startsWith("evt_"),
                "eventTypeId should start with evt_, got: " + result.getEventTypeId());
        assertNotNull(result.getDeliveryIds());
        assertTrue(result.getDeliveryIds().size() >= 1,
                "fan-out should produce at least one delivery");
    }

    @Test
    void trigger_unsubscribedEventType_returnsEmptyDeliveryIds() {
        Map<String, Object> payload = Map.of("data", "noop");
        TriggerOptions opts = new TriggerOptions(payload);

        // Pre-seeded fixture event type with zero subscriptions — shared across
        // all SDK integration tests. See packages/db/src/seeds/test-fixtures.sql
        // section 8b.
        TriggerResult result = client.trigger("event.type.nobody.subscribed.to", opts);

        assertEquals("accepted", result.getStatus());
        assertNotNull(result.getDeliveryIds());
        assertTrue(result.getDeliveryIds().isEmpty(),
                "unsubscribed event type should produce zero deliveries");
    }

    // ---------------------------------------------------------------
    // sendBatch
    // ---------------------------------------------------------------

    @Test
    void sendBatch_twoItems_allAccepted() {
        List<SendBatchItem> items = List.of(
                new SendBatchItem(endpointId, Map.of("batch", 1)),
                new SendBatchItem(endpointId, Map.of("batch", 2))
        );

        BatchResult result = client.sendBatch(items);

        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());
        for (BatchResultItem item : result.getItems()) {
            assertEquals("accepted", item.getStatus());
            assertNotNull(item.getDeliveryId());
            assertTrue(item.getDeliveryId().startsWith("del_"),
                    "batch deliveryId should start with del_, got: " + item.getDeliveryId());
        }
    }

    // ---------------------------------------------------------------
    // triggerBatch
    // ---------------------------------------------------------------

    @Test
    void triggerBatch_twoItems_allAccepted() {
        List<TriggerBatchItem> items = List.of(
                new TriggerBatchItem(eventType, Map.of("batch_trigger", 1)),
                new TriggerBatchItem(eventType, Map.of("batch_trigger", 2))
        );

        BatchResult result = client.triggerBatch(items);

        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());
        for (BatchResultItem item : result.getItems()) {
            assertEquals("accepted", item.getStatus());
            assertNotNull(item.getEventTypeId());
            assertTrue(item.getEventTypeId().startsWith("evt_"),
                    "batch eventTypeId should start with evt_, got: " + item.getEventTypeId());
        }
    }

    // ---------------------------------------------------------------
    // Error cases
    // ---------------------------------------------------------------

    @Test
    void send_invalidApiKey_throws401() {
        NahookClient badClient = new NahookClient(
                "nhk_us_invalid_000000000000",
                ClientOptions.builder().baseUrl(apiUrl).retries(0).build()
        );

        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                badClient.send(endpointId, new SendOptions(Map.of("test", true)))
        );

        assertEquals(401, ex.getStatus());
        assertTrue(ex.isAuthError(), "401 should be an auth error");
    }

    @Test
    void send_disabledApiKey_throws403() {
        NahookClient disabledClient = new NahookClient(
                disabledApiKey,
                ClientOptions.builder().baseUrl(apiUrl).retries(0).build()
        );

        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                disabledClient.send(endpointId, new SendOptions(Map.of("test", true)))
        );

        assertEquals(403, ex.getStatus());
        assertEquals("token_disabled", ex.getCode());
        assertTrue(ex.isAuthError(), "403 with token_disabled should be an auth error");
    }

    @Test
    void send_missingEndpoint_throws404() {
        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                client.send("ep_nonexistent_999999", new SendOptions(Map.of("test", true)))
        );

        assertEquals(404, ex.getStatus());
        assertTrue(ex.isNotFound(), "missing endpoint should be a not-found error");
    }

    @Test
    void trigger_invalidEventType_throws400() {
        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                client.trigger("!!!invalid!!!", new TriggerOptions(Map.of("test", true)))
        );

        assertEquals(400, ex.getStatus());
        assertTrue(ex.isValidationError(), "invalid event type should be a validation error");
    }
}
