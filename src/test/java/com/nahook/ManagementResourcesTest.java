package com.nahook;

import com.nahook.types.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HTTP-level unit tests for all management resources using MockWebServer.
 * Verifies correct HTTP method, URL path, request body, headers, and response deserialization.
 */
class ManagementResourcesTest {

    private static final ObjectMapper MAPPER = HttpClientWrapper.MAPPER;
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

    // ── Helpers ──

    private void enqueueJson(int status, String json) {
        server.enqueue(new MockResponse()
                .setResponseCode(status)
                .setHeader("Content-Type", "application/json")
                .setBody(json));
    }

    private RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    private JsonNode parseBody(RecordedRequest req) throws Exception {
        return MAPPER.readTree(req.getBody().readUtf8());
    }

    // ══════════════════════════════════════════════════════════════════
    // Endpoints
    // ══════════════════════════════════════════════════════════════════

    @Test
    void endpointsList() throws Exception {
        enqueueJson(200, "[{\"id\":\"ep_1\",\"url\":\"https://example.com\",\"isActive\":true,\"type\":\"webhook\"}]");
        var result = mgmt.endpoints().list("ws_123");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints", req.getPath());
        assertEquals("Bearer nhm_test123", req.getHeader("Authorization"));
        assertEquals(1, result.getData().size());
        assertEquals("ep_1", result.getData().get(0).getId());
    }

    @Test
    void endpointsCreate() throws Exception {
        enqueueJson(201, "{\"id\":\"ep_new\",\"url\":\"https://example.com/hook\",\"isActive\":true,\"type\":\"webhook\"}");
        var result = mgmt.endpoints().create("ws_123", CreateEndpointOptions.builder("https://example.com/hook").build());
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints", req.getPath());
        assertEquals("application/json", req.getHeader("Content-Type"));
        var body = parseBody(req);
        assertEquals("https://example.com/hook", body.get("url").asText());
        assertEquals("ep_new", result.getId());
    }

    @Test
    void endpointsGet() throws Exception {
        enqueueJson(200, "{\"id\":\"ep_1\",\"url\":\"https://example.com\",\"isActive\":true,\"type\":\"webhook\"}");
        var result = mgmt.endpoints().get("ws_123", "ep_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints/ep_1", req.getPath());
        assertEquals("ep_1", result.getId());
    }

    @Test
    void endpointsUpdate() throws Exception {
        enqueueJson(200, "{\"id\":\"ep_1\",\"url\":\"https://updated.com\",\"isActive\":true,\"type\":\"webhook\"}");
        var result = mgmt.endpoints().update("ws_123", "ep_1",
                UpdateEndpointOptions.builder().url("https://updated.com").build());
        var req = takeRequest();
        assertEquals("PATCH", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints/ep_1", req.getPath());
        assertEquals("https://updated.com", result.getUrl());
    }

    @Test
    void endpointsDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204));
        mgmt.endpoints().delete("ws_123", "ep_1");
        var req = takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints/ep_1", req.getPath());
    }

    // ══════════════════════════════════════════════════════════════════
    // Event Types
    // ══════════════════════════════════════════════════════════════════

    @Test
    void eventTypesList() throws Exception {
        enqueueJson(200, "[{\"id\":\"evt_1\",\"name\":\"order.created\"}]");
        var result = mgmt.eventTypes().list("ws_123");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/event-types", req.getPath());
        assertEquals(1, result.getData().size());
        assertEquals("order.created", result.getData().get(0).getName());
    }

    @Test
    void eventTypesCreate() throws Exception {
        enqueueJson(201, "{\"id\":\"evt_new\",\"name\":\"order.shipped\"}");
        var result = mgmt.eventTypes().create("ws_123", new CreateEventTypeOptions("order.shipped"));
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/event-types", req.getPath());
        var body = parseBody(req);
        assertEquals("order.shipped", body.get("name").asText());
        assertEquals("evt_new", result.getId());
    }

    @Test
    void eventTypesGet() throws Exception {
        enqueueJson(200, "{\"id\":\"evt_1\",\"name\":\"order.created\"}");
        var result = mgmt.eventTypes().get("ws_123", "evt_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/event-types/evt_1", req.getPath());
        assertEquals("evt_1", result.getId());
    }

    @Test
    void eventTypesUpdate() throws Exception {
        enqueueJson(200, "{\"id\":\"evt_1\",\"name\":\"order.created\",\"description\":\"Updated\"}");
        var result = mgmt.eventTypes().update("ws_123", "evt_1",
                new UpdateEventTypeOptions("Updated"));
        var req = takeRequest();
        assertEquals("PATCH", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/event-types/evt_1", req.getPath());
    }

    @Test
    void eventTypesDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204));
        mgmt.eventTypes().delete("ws_123", "evt_1");
        var req = takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/event-types/evt_1", req.getPath());
    }

    // ══════════════════════════════════════════════════════════════════
    // Applications
    // ══════════════════════════════════════════════════════════════════

    @Test
    void applicationsList() throws Exception {
        enqueueJson(200, "[{\"id\":\"app_1\",\"name\":\"My App\",\"metadata\":{}}]");
        var result = mgmt.applications().list("ws_123");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications", req.getPath());
        assertEquals(1, result.getData().size());
    }

    @Test
    void applicationsListWithPagination() throws Exception {
        enqueueJson(200, "[]");
        mgmt.applications().list("ws_123", new ListOptions(10, 20));
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().contains("limit=10"));
        assertTrue(req.getPath().contains("offset=20"));
    }

    @Test
    void applicationsCreate() throws Exception {
        enqueueJson(201, "{\"id\":\"app_new\",\"name\":\"Acme\",\"metadata\":{}}");
        var result = mgmt.applications().create("ws_123", new CreateApplicationOptions("Acme"));
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications", req.getPath());
        var body = parseBody(req);
        assertEquals("Acme", body.get("name").asText());
        assertEquals("app_new", result.getId());
    }

    @Test
    void applicationsGet() throws Exception {
        enqueueJson(200, "{\"id\":\"app_1\",\"name\":\"My App\",\"metadata\":{}}");
        var result = mgmt.applications().get("ws_123", "app_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications/app_1", req.getPath());
        assertEquals("app_1", result.getId());
    }

    @Test
    void applicationsUpdate() throws Exception {
        enqueueJson(200, "{\"id\":\"app_1\",\"name\":\"Updated\",\"metadata\":{}}");
        var result = mgmt.applications().update("ws_123", "app_1",
                new UpdateApplicationOptions("Updated", null));
        var req = takeRequest();
        assertEquals("PATCH", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications/app_1", req.getPath());
        assertEquals("Updated", result.getName());
    }

    @Test
    void applicationsDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204));
        mgmt.applications().delete("ws_123", "app_1");
        var req = takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications/app_1", req.getPath());
    }

    @Test
    void applicationsListEndpoints() throws Exception {
        enqueueJson(200, "[{\"id\":\"ep_1\",\"url\":\"https://example.com\",\"isActive\":true,\"type\":\"webhook\"}]");
        var result = mgmt.applications().listEndpoints("ws_123", "app_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications/app_1/endpoints", req.getPath());
        assertEquals(1, result.getData().size());
    }

    @Test
    void applicationsCreateEndpoint() throws Exception {
        enqueueJson(201, "{\"id\":\"ep_new\",\"url\":\"https://example.com/hook\",\"isActive\":true,\"type\":\"webhook\"}");
        var result = mgmt.applications().createEndpoint("ws_123", "app_1",
                CreateEndpointOptions.builder("https://example.com/hook").build());
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications/app_1/endpoints", req.getPath());
        assertEquals("ep_new", result.getId());
    }

    // ══════════════════════════════════════════════════════════════════
    // Subscriptions
    // ══════════════════════════════════════════════════════════════════

    @Test
    void subscriptionsList() throws Exception {
        enqueueJson(200, "[{\"id\":\"sub_1\",\"eventTypeId\":\"evt_1\",\"eventTypeName\":\"order.created\",\"createdAt\":\"2026-01-01T00:00:00Z\"}]");
        var result = mgmt.subscriptions().list("ws_123", "ep_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints/ep_1/subscriptions", req.getPath());
        assertEquals(1, result.getData().size());
    }

    @Test
    void subscriptionsCreate() throws Exception {
        enqueueJson(200, "{\"subscribed\":1}");
        var result = mgmt.subscriptions().create("ws_123", "ep_1",
                new CreateSubscriptionOptions("evt_1"));
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints/ep_1/subscriptions", req.getPath());
        var body = parseBody(req);
        assertTrue(body.has("eventTypeIds"));
        assertEquals(1, result.getSubscribed());
    }

    @Test
    void subscriptionsDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204));
        mgmt.subscriptions().delete("ws_123", "ep_1", "evt_1");
        var req = takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/endpoints/ep_1/subscriptions/evt_1", req.getPath());
    }

    // ══════════════════════════════════════════════════════════════════
    // Portal Sessions
    // ══════════════════════════════════════════════════════════════════

    @Test
    void portalSessionsCreate() throws Exception {
        enqueueJson(201, "{\"url\":\"https://portal.nahook.com/s/abc\",\"code\":\"xyz\",\"expiresAt\":\"2026-04-10T12:00:00Z\"}");
        var result = mgmt.portalSessions().create("ws_123", "app_1");
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/applications/app_1/portal", req.getPath());
        assertEquals("xyz", result.getCode());
    }

    @Test
    void portalSessionsCreateWithMetadata() throws Exception {
        enqueueJson(201, "{\"url\":\"https://portal.nahook.com/s/abc\",\"code\":\"xyz\",\"expiresAt\":\"2026-04-10T12:00:00Z\"}");
        var opts = new CreatePortalSessionOptions(java.util.Map.of("userId", "u-1"));
        mgmt.portalSessions().create("ws_123", "app_1", opts);
        var req = takeRequest();
        var body = parseBody(req);
        assertEquals("u-1", body.get("metadata").get("userId").asText());
    }

    // ══════════════════════════════════════════════════════════════════
    // Environments
    // ══════════════════════════════════════════════════════════════════

    @Test
    void environmentsList() throws Exception {
        enqueueJson(200, "[{\"id\":\"env_1\",\"name\":\"Production\",\"slug\":\"production\",\"isDefault\":true}]");
        var result = mgmt.environments().list("ws_123");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments", req.getPath());
        assertEquals(1, result.getData().size());
        assertEquals("env_1", result.getData().get(0).getId());
        assertEquals("Production", result.getData().get(0).getName());
        assertTrue(result.getData().get(0).isDefault());
    }

    @Test
    void environmentsCreate() throws Exception {
        enqueueJson(201, "{\"id\":\"env_new\",\"name\":\"Staging\",\"slug\":\"staging\",\"isDefault\":false}");
        var result = mgmt.environments().create("ws_123",
                new CreateEnvironmentOptions("Staging", "staging"));
        var req = takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments", req.getPath());
        assertEquals("application/json", req.getHeader("Content-Type"));
        var body = parseBody(req);
        assertEquals("Staging", body.get("name").asText());
        assertEquals("staging", body.get("slug").asText());
        assertEquals("env_new", result.getId());
        assertFalse(result.isDefault());
    }

    @Test
    void environmentsGet() throws Exception {
        enqueueJson(200, "{\"id\":\"env_1\",\"name\":\"Production\",\"slug\":\"production\",\"isDefault\":true}");
        var result = mgmt.environments().get("ws_123", "env_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments/env_1", req.getPath());
        assertEquals("env_1", result.getId());
    }

    @Test
    void environmentsUpdate() throws Exception {
        enqueueJson(200, "{\"id\":\"env_1\",\"name\":\"Pre-production\",\"slug\":\"production\",\"isDefault\":true}");
        var result = mgmt.environments().update("ws_123", "env_1",
                UpdateEnvironmentOptions.withName("Pre-production"));
        var req = takeRequest();
        assertEquals("PATCH", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments/env_1", req.getPath());
        var body = parseBody(req);
        assertEquals("Pre-production", body.get("name").asText());
        assertEquals("Pre-production", result.getName());
    }

    @Test
    void environmentsDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204));
        mgmt.environments().delete("ws_123", "env_1");
        var req = takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments/env_1", req.getPath());
    }

    @Test
    void environmentsListEventTypeVisibility() throws Exception {
        enqueueJson(200, "[{\"eventTypeId\":\"evt_1\",\"eventTypeName\":\"order.created\",\"published\":true}]");
        var result = mgmt.environments().listEventTypeVisibility("ws_123", "env_1");
        var req = takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments/env_1/event-types", req.getPath());
        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).isPublished());
        assertEquals("order.created", result.getData().get(0).getEventTypeName());
    }

    @Test
    void environmentsSetEventTypeVisibility() throws Exception {
        enqueueJson(200, "{\"eventTypeId\":\"evt_1\",\"eventTypeName\":\"order.created\",\"published\":true}");
        var result = mgmt.environments().setEventTypeVisibility("ws_123", "env_1", "evt_1",
                new SetVisibilityOptions(true));
        var req = takeRequest();
        assertEquals("PUT", req.getMethod());
        assertEquals("/management/v1/workspaces/ws_123/environments/env_1/event-types/evt_1/visibility", req.getPath());
        assertEquals("application/json", req.getHeader("Content-Type"));
        var body = parseBody(req);
        assertTrue(body.get("published").asBoolean());
        assertTrue(result.isPublished());
    }

    // ══════════════════════════════════════════════════════════════════
    // Headers
    // ══════════════════════════════════════════════════════════════════

    @Test
    void sendsAuthorizationHeader() throws Exception {
        enqueueJson(200, "[]");
        mgmt.endpoints().list("ws_123");
        var req = takeRequest();
        assertEquals("Bearer nhm_test123", req.getHeader("Authorization"));
    }

    @Test
    void sendsUserAgentHeader() throws Exception {
        enqueueJson(200, "[]");
        mgmt.endpoints().list("ws_123");
        var req = takeRequest();
        assertTrue(req.getHeader("User-Agent").startsWith("nahook-java/"));
    }

    @Test
    void sendsContentTypeOnPostRequests() throws Exception {
        enqueueJson(201, "{\"id\":\"ep_new\",\"url\":\"https://example.com\",\"isActive\":true,\"type\":\"webhook\"}");
        mgmt.endpoints().create("ws_123", CreateEndpointOptions.builder("https://example.com").build());
        var req = takeRequest();
        assertEquals("application/json", req.getHeader("Content-Type"));
    }

    @Test
    void omitsContentTypeOnGetRequests() throws Exception {
        enqueueJson(200, "[]");
        mgmt.endpoints().list("ws_123");
        var req = takeRequest();
        assertNull(req.getHeader("Content-Type"));
    }
}
