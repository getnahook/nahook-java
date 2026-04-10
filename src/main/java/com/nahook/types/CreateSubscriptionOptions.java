package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateSubscriptionOptions {
    @JsonProperty("eventTypeId") private final String eventTypeId;

    public CreateSubscriptionOptions(String eventTypeId) { this.eventTypeId = eventTypeId; }
    public String getEventTypeId() { return eventTypeId; }
}
