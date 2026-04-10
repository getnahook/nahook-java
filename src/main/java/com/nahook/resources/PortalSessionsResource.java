package com.nahook.resources;

import com.nahook.HttpClientWrapper;
import com.nahook.types.CreatePortalSessionOptions;
import com.nahook.types.PortalSession;

import java.util.Collections;

public class PortalSessionsResource {

    private final HttpClientWrapper http;

    public PortalSessionsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public PortalSession create(String workspaceId, String appId) {
        return create(workspaceId, appId, null);
    }

    public PortalSession create(String workspaceId, String appId, CreatePortalSessionOptions options) {
        String path = "/management/v1/workspaces/" + HttpClientWrapper.encodePath(workspaceId)
                + "/applications/" + HttpClientWrapper.encodePath(appId) + "/portal";
        Object body = options != null ? options : Collections.emptyMap();
        return http.request("POST", path, body, PortalSession.class);
    }
}
