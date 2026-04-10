package com.nahook.resources;

import com.nahook.HttpClientWrapper;
import com.nahook.types.*;

import java.util.Arrays;

public class EventTypesResource {

    private final HttpClientWrapper http;

    public EventTypesResource(HttpClientWrapper http) {
        this.http = http;
    }

    public ListResult<EventType> list(String workspaceId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/event-types";
        EventType[] arr = http.request("GET", path, null, EventType[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public EventType create(String workspaceId, CreateEventTypeOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/event-types";
        return http.request("POST", path, options, EventType.class);
    }

    public EventType get(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/event-types/" + HttpClientWrapper.encodePath(id);
        return http.request("GET", path, null, EventType.class);
    }

    public EventType update(String workspaceId, String id, UpdateEventTypeOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/event-types/" + HttpClientWrapper.encodePath(id);
        return http.request("PATCH", path, options, EventType.class);
    }

    public void delete(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/event-types/" + HttpClientWrapper.encodePath(id);
        http.request("DELETE", path, null, Void.class);
    }
}
