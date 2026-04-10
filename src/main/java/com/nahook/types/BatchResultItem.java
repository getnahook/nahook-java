package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BatchResultItem {

    private final int index;
    private final String deliveryId;
    private final String idempotencyKey;
    private final String eventTypeId;
    private final List<String> deliveryIds;
    private final String status;
    private final BatchItemError error;

    @JsonCreator
    public BatchResultItem(
            @JsonProperty("index") int index,
            @JsonProperty("deliveryId") String deliveryId,
            @JsonProperty("idempotencyKey") String idempotencyKey,
            @JsonProperty("eventTypeId") String eventTypeId,
            @JsonProperty("deliveryIds") List<String> deliveryIds,
            @JsonProperty("status") String status,
            @JsonProperty("error") BatchItemError error) {
        this.index = index;
        this.deliveryId = deliveryId;
        this.idempotencyKey = idempotencyKey;
        this.eventTypeId = eventTypeId;
        this.deliveryIds = deliveryIds;
        this.status = status;
        this.error = error;
    }

    public int getIndex() { return index; }
    public String getDeliveryId() { return deliveryId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getEventTypeId() { return eventTypeId; }
    public List<String> getDeliveryIds() { return deliveryIds; }
    public String getStatus() { return status; }
    public BatchItemError getError() { return error; }
}
