package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateApplicationOptions {
    @JsonProperty("name") private final String name;
    @JsonProperty("metadata") private final Map<String, String> metadata;

    public UpdateApplicationOptions(String name, Map<String, String> metadata) {
        this.name = name; this.metadata = metadata;
    }
}
