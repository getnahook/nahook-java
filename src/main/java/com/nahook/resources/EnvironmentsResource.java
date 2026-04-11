package com.nahook.resources;

import com.nahook.HttpClientWrapper;
import com.nahook.types.*;

import java.util.Arrays;

public class EnvironmentsResource {

    private final HttpClientWrapper http;

    public EnvironmentsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public ListResult<Environment> list(String workspaceId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/environments";
        Environment[] arr = http.request("GET", path, null, Environment[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public Environment create(String workspaceId, CreateEnvironmentOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/environments";
        return http.request("POST", path, options, Environment.class);
    }

    public Environment get(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/environments/" + HttpClientWrapper.encodePath(id);
        return http.request("GET", path, null, Environment.class);
    }

    public Environment update(String workspaceId, String id, UpdateEnvironmentOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/environments/" + HttpClientWrapper.encodePath(id);
        return http.request("PATCH", path, options, Environment.class);
    }

    public void delete(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/environments/" + HttpClientWrapper.encodePath(id);
        http.request("DELETE", path, null, Void.class);
    }

    public ListResult<EventTypeVisibility> listEventTypeVisibility(String workspaceId, String envId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/environments/" + HttpClientWrapper.encodePath(envId) + "/event-types";
        EventTypeVisibility[] arr = http.request("GET", path, null, EventTypeVisibility[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public EventTypeVisibility setEventTypeVisibility(String workspaceId, String envId,
            String eventTypeId, SetVisibilityOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/environments/" + HttpClientWrapper.encodePath(envId)
                + "/event-types/" + HttpClientWrapper.encodePath(eventTypeId) + "/visibility";
        return http.request("PUT", path, options, EventTypeVisibility.class);
    }
}
