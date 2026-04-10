package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateApplicationOptions {
    @JsonProperty("name") private final String name;
    @JsonProperty("externalId") private final String externalId;
    @JsonProperty("metadata") private final Map<String, String> metadata;

    public CreateApplicationOptions(String name) { this(name, null, null); }
    public CreateApplicationOptions(String name, String externalId, Map<String, String> metadata) {
        this.name = name; this.externalId = externalId; this.metadata = metadata;
    }
}
