package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Endpoint {
    private final String id;
    private final String url;
    private final String description;
    private final boolean isActive;
    private final String type;
    private final Map<String, Object> config;
    private final String secret;
    private final Map<String, String> metadata;
    private final String createdAt;
    private final String updatedAt;

    @JsonCreator
    public Endpoint(
            @JsonProperty("id") String id, @JsonProperty("url") String url,
            @JsonProperty("description") String description, @JsonProperty("isActive") boolean isActive,
            @JsonProperty("type") String type, @JsonProperty("config") Map<String, Object> config,
            @JsonProperty("secret") String secret, @JsonProperty("metadata") Map<String, String> metadata,
            @JsonProperty("createdAt") String createdAt, @JsonProperty("updatedAt") String updatedAt) {
        this.id = id; this.url = url; this.description = description; this.isActive = isActive;
        this.type = type; this.config = config; this.secret = secret; this.metadata = metadata;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getUrl() { return url; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public String getType() { return type; }
    public Map<String, Object> getConfig() { return config; }
    public String getSecret() { return secret; }
    public Map<String, String> getMetadata() { return metadata; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
