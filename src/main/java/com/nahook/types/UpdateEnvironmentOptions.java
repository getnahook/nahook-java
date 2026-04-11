package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEnvironmentOptions {
    @JsonProperty("name") private final String name;

    public UpdateEnvironmentOptions(String name) {
        this.name = name;
    }
}
