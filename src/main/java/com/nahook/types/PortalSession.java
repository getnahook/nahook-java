package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PortalSession {
    private final String url;
    private final String code;
    private final String expiresAt;

    @JsonCreator
    public PortalSession(@JsonProperty("url") String url, @JsonProperty("code") String code,
                         @JsonProperty("expiresAt") String expiresAt) {
        this.url = url; this.code = code; this.expiresAt = expiresAt;
    }

    public String getUrl() { return url; }
    public String getCode() { return code; }
    public String getExpiresAt() { return expiresAt; }
}
