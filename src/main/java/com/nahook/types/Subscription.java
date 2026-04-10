package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription {
    private final String id;
    private final String endpointId;
    private final String eventTypeId;
    private final String createdAt;

    @JsonCreator
    public Subscription(@JsonProperty("id") String id, @JsonProperty("endpointId") String endpointId,
                        @JsonProperty("eventTypeId") String eventTypeId, @JsonProperty("createdAt") String createdAt) {
        this.id = id; this.endpointId = endpointId; this.eventTypeId = eventTypeId; this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getEndpointId() { return endpointId; }
    public String getEventTypeId() { return eventTypeId; }
    public String getCreatedAt() { return createdAt; }
}
