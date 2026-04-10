package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePortalSessionOptions {
    @JsonProperty("metadata") private final Map<String, String> metadata;

    public CreatePortalSessionOptions(Map<String, String> metadata) { this.metadata = metadata; }
}
