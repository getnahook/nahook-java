package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateEnvironmentOptions {
    @JsonProperty("name") private final String name;
    @JsonProperty("slug") private final String slug;

    public CreateEnvironmentOptions(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
