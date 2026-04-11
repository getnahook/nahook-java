package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription {
    private final String id;
    private final String eventTypeId;
    private final String eventTypeName;
    private final String createdAt;

    @JsonCreator
    public Subscription(@JsonProperty("id") String id,
                        @JsonProperty("eventTypeId") String eventTypeId,
                        @JsonProperty("eventTypeName") String eventTypeName,
                        @JsonProperty("createdAt") String createdAt) {
        this.id = id;
        this.eventTypeId = eventTypeId;
        this.eventTypeName = eventTypeName;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getEventTypeId() { return eventTypeId; }
    public String getEventTypeName() { return eventTypeName; }
    public String getCreatedAt() { return createdAt; }
}
