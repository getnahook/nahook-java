package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Environment {
    private final String id;
    private final String name;
    private final String slug;
    private final boolean isDefault;
    private final String createdAt;
    private final String updatedAt;

    @JsonCreator
    public Environment(
            @JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("slug") String slug, @JsonProperty("isDefault") boolean isDefault,
            @JsonProperty("createdAt") String createdAt, @JsonProperty("updatedAt") String updatedAt) {
        this.id = id; this.name = name; this.slug = slug; this.isDefault = isDefault;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public boolean isDefault() { return isDefault; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
