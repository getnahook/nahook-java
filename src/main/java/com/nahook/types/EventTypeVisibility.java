package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventTypeVisibility {
    private final String eventTypeId;
    private final String eventTypeName;
    private final boolean published;

    @JsonCreator
    public EventTypeVisibility(
            @JsonProperty("eventTypeId") String eventTypeId,
            @JsonProperty("eventTypeName") String eventTypeName,
            @JsonProperty("published") boolean published) {
        this.eventTypeId = eventTypeId;
        this.eventTypeName = eventTypeName;
        this.published = published;
    }

    public String getEventTypeId() { return eventTypeId; }
    public String getEventTypeName() { return eventTypeName; }
    public boolean isPublished() { return published; }
}
