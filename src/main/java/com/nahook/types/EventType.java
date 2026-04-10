package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventType {
    private final String id;
    private final String name;
    private final String description;
    private final String createdAt;

    @JsonCreator
    public EventType(@JsonProperty("id") String id, @JsonProperty("name") String name,
                     @JsonProperty("description") String description, @JsonProperty("createdAt") String createdAt) {
        this.id = id; this.name = name; this.description = description; this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
}
