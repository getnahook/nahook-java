package com.nahook.integration;

import com.nahook.NahookManagement;
import com.nahook.errors.NahookApiException;
import com.nahook.types.*;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Management API integration tests that hit a real Nahook API instance.
 * Skipped automatically when NAHOOK_TEST_* env vars are not set.
 */
@Tag("management-integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ManagementIntegrationTest {

    private static String apiUrl;
    private static String mgmtToken;
    private static String workspaceId;

    private static NahookManagement mgmt;

    @BeforeAll
    static void setUp() {
        apiUrl = System.getenv("NAHOOK_TEST_API_URL");
        mgmtToken = System.getenv("NAHOOK_TEST_MGMT_TOKEN");
        workspaceId = System.getenv("NAHOOK_TEST_WORKSPACE_ID");

        assumeTrue(apiUrl != null && !apiUrl.isEmpty(), "NAHOOK_TEST_API_URL not set");
        assumeTrue(mgmtToken != null && !mgmtToken.isEmpty(), "NAHOOK_TEST_MGMT_TOKEN not set");
        assumeTrue(workspaceId != null && !workspaceId.isEmpty(), "NAHOOK_TEST_WORKSPACE_ID not set");

        mgmt = NahookManagement.builder(mgmtToken)
                .baseUrl(apiUrl)
                .build();
    }

    // ---------------------------------------------------------------
    // Event Types CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(1)
    void eventTypes_crud_fullLifecycle() {
        long ts = System.currentTimeMillis();
        String name = "mgmt.test.event." + ts;

        // Create
        EventType created = mgmt.eventTypes().create(workspaceId,
                new CreateEventTypeOptions(name, "Integration test event type"));
        assertNotNull(created.getId());
        assertEquals(name, created.getName());
        assertEquals("Integration test event type", created.getDescription());

        String eventTypeId = created.getId();

        // List — should contain the created event type
        ListResult<EventType> list = mgmt.eventTypes().list(workspaceId);
        assertNotNull(list.getData());
        assertTrue(list.getData().stream().anyMatch(et -> et.getId().equals(eventTypeId)),
                "listed event types should contain the newly created one");

        // Get
        EventType fetched = mgmt.eventTypes().get(workspaceId, eventTypeId);
        assertEquals(eventTypeId, fetched.getId());
        assertEquals(name, fetched.getName());

        // Update
        EventType updated = mgmt.eventTypes().update(workspaceId, eventTypeId,
                new UpdateEventTypeOptions("Updated description " + ts));
        assertEquals(eventTypeId, updated.getId());
        assertEquals("Updated description " + ts, updated.getDescription());

        // Delete
        assertDoesNotThrow(() -> mgmt.eventTypes().delete(workspaceId, eventTypeId));

        // Verify 404 on deleted
        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                mgmt.eventTypes().get(workspaceId, eventTypeId));
        assertEquals(404, ex.getStatus());
        assertTrue(ex.isNotFound());
    }

    // ---------------------------------------------------------------
    // Endpoints CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(2)
    void endpoints_crud_fullLifecycle() {
        long ts = System.currentTimeMillis();

        // Create
        CreateEndpointOptions createOpts = CreateEndpointOptions.builder("https://example.com/webhook/" + ts)
                .description("Integration test endpoint")
                .build();
        Endpoint created = mgmt.endpoints().create(workspaceId, createOpts);
        assertNotNull(created.getId());
        assertTrue(created.getId().startsWith("ep_"),
                "endpoint id should start with ep_, got: " + created.getId());
        assertTrue(created.getUrl().contains(String.valueOf(ts)));

        String endpointId = created.getId();

        // List — should contain the created endpoint
        ListResult<Endpoint> list = mgmt.endpoints().list(workspaceId);
        assertNotNull(list.getData());
        assertTrue(list.getData().stream().anyMatch(ep -> ep.getId().equals(endpointId)),
                "listed endpoints should contain the newly created one");

        // Get
        Endpoint fetched = mgmt.endpoints().get(workspaceId, endpointId);
        assertEquals(endpointId, fetched.getId());
        assertEquals("Integration test endpoint", fetched.getDescription());

        // Update
        UpdateEndpointOptions updateOpts = UpdateEndpointOptions.builder()
                .description("Updated endpoint " + ts)
                .build();
        Endpoint updated = mgmt.endpoints().update(workspaceId, endpointId, updateOpts);
        assertEquals(endpointId, updated.getId());
        assertEquals("Updated endpoint " + ts, updated.getDescription());

        // Delete
        assertDoesNotThrow(() -> mgmt.endpoints().delete(workspaceId, endpointId));

        // Verify 404 on deleted
        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                mgmt.endpoints().get(workspaceId, endpointId));
        assertEquals(404, ex.getStatus());
        assertTrue(ex.isNotFound());
    }

    // ---------------------------------------------------------------
    // Applications CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(3)
    void applications_crud_fullLifecycle() {
        long ts = System.currentTimeMillis();
        String appName = "Test App " + ts;

        // Create
        Application created = mgmt.applications().create(workspaceId,
                new CreateApplicationOptions(appName));
        assertNotNull(created.getId());
        assertTrue(created.getId().startsWith("app_"),
                "application id should start with app_, got: " + created.getId());
        assertEquals(appName, created.getName());

        String appId = created.getId();

        // List — should contain the created application
        ListResult<Application> list = mgmt.applications().list(workspaceId);
        assertNotNull(list.getData());
        assertTrue(list.getData().stream().anyMatch(app -> app.getId().equals(appId)),
                "listed applications should contain the newly created one");

        // Get
        Application fetched = mgmt.applications().get(workspaceId, appId);
        assertEquals(appId, fetched.getId());
        assertEquals(appName, fetched.getName());

        // Update
        Application updated = mgmt.applications().update(workspaceId, appId,
                new UpdateApplicationOptions("Updated App " + ts, null));
        assertEquals(appId, updated.getId());
        assertEquals("Updated App " + ts, updated.getName());

        // Delete
        assertDoesNotThrow(() -> mgmt.applications().delete(workspaceId, appId));

        // Verify 404 on deleted
        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                mgmt.applications().get(workspaceId, appId));
        assertEquals(404, ex.getStatus());
        assertTrue(ex.isNotFound());
    }

    // ---------------------------------------------------------------
    // Subscriptions: create endpoint + event type, subscribe, list, unsubscribe
    // ---------------------------------------------------------------

    @Test
    @Order(4)
    void subscriptions_fullLifecycle() {
        long ts = System.currentTimeMillis();

        // Create an endpoint for the subscription test
        CreateEndpointOptions epOpts = CreateEndpointOptions.builder("https://example.com/sub-test/" + ts)
                .description("Subscription test endpoint")
                .build();
        Endpoint endpoint = mgmt.endpoints().create(workspaceId, epOpts);
        assertNotNull(endpoint.getId());

        // Create an event type for the subscription test
        String eventTypeName = "sub.test.event." + ts;
        EventType eventType = mgmt.eventTypes().create(workspaceId,
                new CreateEventTypeOptions(eventTypeName, "Subscription test event type"));
        assertNotNull(eventType.getId());

        String endpointId = endpoint.getId();
        String eventTypeId = eventType.getId();

        try {
            // Subscribe — API expects eventTypeIds array, returns {subscribed: N}
            SubscribeResult result = mgmt.subscriptions().create(workspaceId, endpointId,
                    new CreateSubscriptionOptions(eventTypeId));
            assertEquals(1, result.getSubscribed());

            // List subscriptions — should contain the one we just created
            ListResult<Subscription> list = mgmt.subscriptions().list(workspaceId, endpointId);
            assertNotNull(list.getData());
            assertTrue(list.getData().stream().anyMatch(s -> s.getEventTypeId().equals(eventTypeId)),
                    "listed subscriptions should contain the newly created one");

            // Verify eventTypeName is present
            Subscription sub = list.getData().stream()
                    .filter(s -> s.getEventTypeId().equals(eventTypeId))
                    .findFirst()
                    .orElseThrow();
            assertEquals(eventTypeName, sub.getEventTypeName());

            // Unsubscribe — uses event type public_id in URL path, returns 204
            assertDoesNotThrow(() ->
                    mgmt.subscriptions().delete(workspaceId, endpointId, eventTypeId));

            // List again — should no longer contain the subscription
            ListResult<Subscription> listAfter = mgmt.subscriptions().list(workspaceId, endpointId);
            assertFalse(listAfter.getData().stream().anyMatch(s -> s.getEventTypeId().equals(eventTypeId)),
                    "subscriptions should not contain the deleted one");
        } finally {
            // Cleanup: delete endpoint and event type
            try { mgmt.endpoints().delete(workspaceId, endpointId); } catch (Exception ignored) {}
            try { mgmt.eventTypes().delete(workspaceId, eventTypeId); } catch (Exception ignored) {}
        }
    }

    // ---------------------------------------------------------------
    // Environments CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(5)
    void environments_crud_fullLifecycle() {
        long ts = System.currentTimeMillis();
        String envName = "Test Env " + ts;
        String envSlug = "test-env-" + ts;

        // Create
        Environment created = mgmt.environments().create(workspaceId,
                new CreateEnvironmentOptions(envName, envSlug));
        assertNotNull(created.getId());
        assertEquals(envName, created.getName());
        assertEquals(envSlug, created.getSlug());

        String envId = created.getId();

        // List — should contain at least the default env + the newly created one
        ListResult<Environment> list = mgmt.environments().list(workspaceId);
        assertNotNull(list.getData());
        assertTrue(list.getData().size() >= 2,
                "should have at least 2 environments (default + created)");
        assertTrue(list.getData().stream().anyMatch(e -> e.getId().equals(envId)),
                "listed environments should contain the newly created one");

        // Get
        Environment fetched = mgmt.environments().get(workspaceId, envId);
        assertEquals(envId, fetched.getId());
        assertEquals(envName, fetched.getName());

        // Update
        String updatedName = "Updated Env " + ts;
        Environment updated = mgmt.environments().update(workspaceId, envId,
                new UpdateEnvironmentOptions(updatedName));
        assertEquals(envId, updated.getId());
        assertEquals(updatedName, updated.getName());

        // Delete
        assertDoesNotThrow(() -> mgmt.environments().delete(workspaceId, envId));

        // Verify 404 on deleted
        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                mgmt.environments().get(workspaceId, envId));
        assertEquals(404, ex.getStatus());
        assertTrue(ex.isNotFound());
    }

    // ---------------------------------------------------------------
    // Event Type Visibility
    // ---------------------------------------------------------------

    @Test
    @Order(6)
    void environments_eventTypeVisibility() {
        long ts = System.currentTimeMillis();

        // Create an environment and an event type for the visibility test
        Environment env = mgmt.environments().create(workspaceId,
                new CreateEnvironmentOptions("Vis Env " + ts, "vis-env-" + ts));
        assertNotNull(env.getId());

        String eventTypeName = "vis.test.event." + ts;
        EventType eventType = mgmt.eventTypes().create(workspaceId,
                new CreateEventTypeOptions(eventTypeName, "Visibility test event type"));
        assertNotNull(eventType.getId());

        String envId = env.getId();
        String eventTypeId = eventType.getId();

        try {
            // List visibility — should contain the event type
            ListResult<EventTypeVisibility> visList = mgmt.environments()
                    .listEventTypeVisibility(workspaceId, envId);
            assertNotNull(visList.getData());
            assertTrue(visList.getData().stream().anyMatch(v -> v.getEventTypeId().equals(eventTypeId)),
                    "visibility list should contain the created event type");

            // Set published = true
            EventTypeVisibility result = mgmt.environments()
                    .setEventTypeVisibility(workspaceId, envId, eventTypeId,
                            new SetVisibilityOptions(true));
            assertEquals(eventTypeId, result.getEventTypeId());
            assertTrue(result.isPublished());

            // Verify via list
            ListResult<EventTypeVisibility> visListAfter = mgmt.environments()
                    .listEventTypeVisibility(workspaceId, envId);
            EventTypeVisibility found = visListAfter.getData().stream()
                    .filter(v -> v.getEventTypeId().equals(eventTypeId))
                    .findFirst()
                    .orElseThrow();
            assertTrue(found.isPublished());
        } finally {
            try { mgmt.environments().delete(workspaceId, envId); } catch (Exception ignored) {}
            try { mgmt.eventTypes().delete(workspaceId, eventTypeId); } catch (Exception ignored) {}
        }
    }

    // ---------------------------------------------------------------
    // Auth error: bad token
    // ---------------------------------------------------------------

    @Test
    @Order(7)
    void invalidToken_throws401() {
        NahookManagement badMgmt = NahookManagement.builder("nhm_invalid_000000000000")
                .baseUrl(apiUrl)
                .build();

        NahookApiException ex = assertThrows(NahookApiException.class, () ->
                badMgmt.eventTypes().list(workspaceId));

        assertEquals(401, ex.getStatus());
        assertTrue(ex.isAuthError(), "invalid management token should produce an auth error");
    }
}
