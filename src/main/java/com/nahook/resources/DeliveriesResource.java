package com.nahook.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nahook.HttpClientWrapper;
import com.nahook.types.Delivery;
import com.nahook.types.DeliveryAttempt;
import com.nahook.types.DeliveryWithPayload;
import com.nahook.types.GetDeliveryOptions;
import com.nahook.types.ListDeliveriesOptions;
import com.nahook.types.PaginatedResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read access to a workspace's webhook deliveries. All methods are paginated or
 * single-resource reads — there is no create/update/delete on this resource.
 *
 * <p>Deliveries are scoped to an endpoint for {@link #list} because the regional
 * deliveries table is indexed by endpoint; there is no workspace-wide index.
 * Single {@link #get} and {@link #getAttempts} accept a delivery public id directly.
 */
public class DeliveriesResource {

    private final HttpClientWrapper http;

    public DeliveriesResource(HttpClientWrapper http) {
        this.http = http;
    }

    public PaginatedResult<Delivery> list(String workspaceId, String endpointId) {
        return list(workspaceId, endpointId, null);
    }

    public PaginatedResult<Delivery> list(String workspaceId, String endpointId, ListDeliveriesOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(endpointId) + "/deliveries";
        Map<String, Object> query = null;
        if (options != null) {
            query = new LinkedHashMap<>();
            if (options.getLimit() != null) query.put("limit", options.getLimit());
            if (options.getCursor() != null) query.put("cursor", options.getCursor());
            if (options.getStatus() != null) query.put("status", options.getStatus());
            if (query.isEmpty()) query = null;
        }
        ListDeliveriesPage page = http.request("GET", path, null, query, ListDeliveriesPage.class);
        List<Delivery> data = page.deliveries != null
                ? Arrays.asList(page.deliveries)
                : java.util.Collections.emptyList();
        return new PaginatedResult<>(data, page.nextCursor);
    }

    public DeliveryWithPayload get(String workspaceId, String deliveryId) {
        return get(workspaceId, deliveryId, null);
    }

    public DeliveryWithPayload get(String workspaceId, String deliveryId, GetDeliveryOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/deliveries/" + HttpClientWrapper.encodePath(deliveryId);
        Map<String, Object> query = null;
        if (options != null && Boolean.TRUE.equals(options.getIncludePayload())) {
            query = new LinkedHashMap<>();
            query.put("include", "payload");
        }
        return http.request("GET", path, null, query, DeliveryWithPayload.class);
    }

    public List<DeliveryAttempt> getAttempts(String workspaceId, String deliveryId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/deliveries/" + HttpClientWrapper.encodePath(deliveryId) + "/attempts";
        DeliveryAttempt[] arr = http.request("GET", path, null, DeliveryAttempt[].class);
        return Arrays.asList(arr);
    }

    /**
     * Wire shape returned by the list endpoint. The API uses {@code deliveries}
     * as the array key; the SDK exposes {@code data} via {@link PaginatedResult}
     * for cross-resource consistency.
     */
    static final class ListDeliveriesPage {
        final Delivery[] deliveries;
        final String nextCursor;

        @JsonCreator
        ListDeliveriesPage(@JsonProperty("deliveries") Delivery[] deliveries,
                           @JsonProperty("nextCursor") String nextCursor) {
            this.deliveries = deliveries;
            this.nextCursor = nextCursor;
        }
    }
}
