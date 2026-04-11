package com.nahook.resources;

import com.nahook.HttpClientWrapper;
import com.nahook.types.*;

import java.util.Arrays;

public class SubscriptionsResource {

    private final HttpClientWrapper http;

    public SubscriptionsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public ListResult<Subscription> list(String workspaceId, String endpointId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(endpointId) + "/subscriptions";
        Subscription[] arr = http.request("GET", path, null, Subscription[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public SubscribeResult create(String workspaceId, String endpointId, CreateSubscriptionOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(endpointId) + "/subscriptions";
        return http.request("POST", path, options, SubscribeResult.class);
    }

    public void delete(String workspaceId, String endpointId, String eventTypeId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(endpointId)
                + "/subscriptions/" + HttpClientWrapper.encodePath(eventTypeId);
        http.request("DELETE", path, null, Void.class);
    }
}
