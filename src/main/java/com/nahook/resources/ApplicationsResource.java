package com.nahook.resources;

import com.nahook.HttpClientWrapper;
import com.nahook.types.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationsResource {

    private final HttpClientWrapper http;

    public ApplicationsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public ListResult<Application> list(String workspaceId) {
        return list(workspaceId, null);
    }

    public ListResult<Application> list(String workspaceId, ListOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/applications";
        Map<String, Object> query = null;
        if (options != null) {
            query = new LinkedHashMap<>();
            if (options.getLimit() != null) query.put("limit", options.getLimit());
            if (options.getOffset() != null) query.put("offset", options.getOffset());
        }
        Application[] arr = http.request("GET", path, null, query, Application[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public Application create(String workspaceId, CreateApplicationOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId) + "/applications";
        return http.request("POST", path, options, Application.class);
    }

    public Application get(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/applications/" + HttpClientWrapper.encodePath(id);
        return http.request("GET", path, null, Application.class);
    }

    public Application update(String workspaceId, String id, UpdateApplicationOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/applications/" + HttpClientWrapper.encodePath(id);
        return http.request("PATCH", path, options, Application.class);
    }

    public void delete(String workspaceId, String id) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/applications/" + HttpClientWrapper.encodePath(id);
        http.request("DELETE", path, null, Void.class);
    }

    public ListResult<Endpoint> listEndpoints(String workspaceId, String appId) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/applications/" + HttpClientWrapper.encodePath(appId) + "/endpoints";
        Endpoint[] arr = http.request("GET", path, null, Endpoint[].class);
        return new ListResult<>(Arrays.asList(arr));
    }

    public Endpoint createEndpoint(String workspaceId, String appId, CreateEndpointOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/applications/" + HttpClientWrapper.encodePath(appId) + "/endpoints";
        return http.request("POST", path, options, Endpoint.class);
    }
}
