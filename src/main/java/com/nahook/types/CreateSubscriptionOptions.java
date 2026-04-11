package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class CreateSubscriptionOptions {
    @JsonProperty("eventTypeIds") private final List<String> eventTypeIds;

    public CreateSubscriptionOptions(List<String> eventTypeIds) {
        this.eventTypeIds = eventTypeIds;
    }

    public CreateSubscriptionOptions(String... eventTypeIds) {
        this.eventTypeIds = Arrays.asList(eventTypeIds);
    }

    public List<String> getEventTypeIds() { return eventTypeIds; }
}
