package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventTypeOptions {
    @JsonProperty("description") private final String description;

    public UpdateEventTypeOptions(String description) { this.description = description; }
}
