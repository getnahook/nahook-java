package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TriggerResult {

    private final String eventTypeId;
    private final List<String> deliveryIds;
    private final String status;

    @JsonCreator
    public TriggerResult(
            @JsonProperty("eventTypeId") String eventTypeId,
            @JsonProperty("deliveryIds") List<String> deliveryIds,
            @JsonProperty("status") String status) {
        this.eventTypeId = eventTypeId;
        this.deliveryIds = deliveryIds;
        this.status = status;
    }

    public String getEventTypeId() { return eventTypeId; }
    public List<String> getDeliveryIds() { return deliveryIds; }
    public String getStatus() { return status; }
}
