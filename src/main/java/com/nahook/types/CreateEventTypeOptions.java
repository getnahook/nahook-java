package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateEventTypeOptions {
    @JsonProperty("name") private final String name;
    @JsonProperty("description") private final String description;

    public CreateEventTypeOptions(String name) { this(name, null); }
    public CreateEventTypeOptions(String name, String description) {
        this.name = name; this.description = description;
    }
}
