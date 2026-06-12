package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Application {
    private final String id;
    private final String externalId;
    private final String name;
    private final Map<String, String> metadata;
    private final Integer maxEndpoints;
    private final boolean showEventTypes;
    private final String createdAt;
    private final String updatedAt;

    @JsonCreator
    public Application(@JsonProperty("id") String id, @JsonProperty("externalId") String externalId,
                       @JsonProperty("name") String name, @JsonProperty("metadata") Map<String, String> metadata,
                       @JsonProperty("maxEndpoints") Integer maxEndpoints,
                       @JsonProperty("showEventTypes") Boolean showEventTypes,
                       @JsonProperty("createdAt") String createdAt, @JsonProperty("updatedAt") String updatedAt) {
        this.id = id; this.externalId = externalId; this.name = name;
        this.metadata = metadata;
        this.maxEndpoints = maxEndpoints;
        // Server default is true; tolerate responses without the field.
        this.showEventTypes = showEventTypes == null || showEventTypes;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getExternalId() { return externalId; }
    public String getName() { return name; }
    public Map<String, String> getMetadata() { return metadata; }
    /** Maximum endpoints this application may have (disabled endpoints count). {@code null} = unlimited. */
    public Integer getMaxEndpoints() { return maxEndpoints; }
    /** Whether the Developer Portal exposes the event-type catalog to this application. */
    public boolean isShowEventTypes() { return showEventTypes; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
