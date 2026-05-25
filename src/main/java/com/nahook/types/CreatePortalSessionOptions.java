package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePortalSessionOptions {
    @JsonProperty("metadata") private final Map<String, String> metadata;
    @JsonProperty("role") private final String role;
    @JsonProperty("expiresInMinutes") private final Integer expiresInMinutes;

    public CreatePortalSessionOptions(Map<String, String> metadata, String role, Integer expiresInMinutes) {
        this.metadata = metadata;
        this.role = role;
        this.expiresInMinutes = expiresInMinutes;
    }

    /** Backward-compatible constructor preserved for v0.1.0 callers. */
    public CreatePortalSessionOptions(Map<String, String> metadata) {
        this(metadata, null, null);
    }
}
