package com.nahook.resources;

import com.nahook.HttpClientWrapper;
import com.nahook.types.*;

import java.util.Arrays;

public class EndpointsResource {

    private final HttpClientWrapper http;

    public EndpointsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public ListResult<Endpoint> list(String workspaceId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/endpoints";
        Endpoint[] arr = http.request("GET", path, null, Endpoint[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public Endpoint create(String workspaceId, CreateEndpointOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/endpoints";
        return http.request("POST", path, options, Endpoint.class);
    }

    public Endpoint get(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(id);
        return http.request("GET", path, null, Endpoint.class);
    }

    public Endpoint update(String workspaceId, String id, UpdateEndpointOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(id);
        return http.request("PATCH", path, options, Endpoint.class);
    }

    public void delete(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/endpoints/" + HttpClientWrapper.encodePath(id);
        http.request("DELETE", path, null, Void.class);
    }
}
