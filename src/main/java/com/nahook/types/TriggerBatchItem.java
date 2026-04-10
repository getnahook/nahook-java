package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TriggerBatchItem {

    @JsonProperty("eventType")
    private final String eventType;

    @JsonProperty("payload")
    private final Map<String, Object> payload;

    @JsonProperty("metadata")
    private final Map<String, String> metadata;

    public TriggerBatchItem(String eventType, Map<String, Object> payload) {
        this(eventType, payload, null);
    }

    public TriggerBatchItem(String eventType, Map<String, Object> payload, Map<String, String> metadata) {
        this.eventType = eventType;
        this.payload = payload;
        this.metadata = metadata;
    }

    public String getEventType() { return eventType; }
    public Map<String, Object> getPayload() { return payload; }
    public Map<String, String> getMetadata() { return metadata; }
}
