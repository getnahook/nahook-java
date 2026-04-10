package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SendResult {

    private final String deliveryId;
    private final String idempotencyKey;
    private final String status;

    @JsonCreator
    public SendResult(
            @JsonProperty("deliveryId") String deliveryId,
            @JsonProperty("idempotencyKey") String idempotencyKey,
            @JsonProperty("status") String status) {
        this.deliveryId = deliveryId;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
    }

    public String getDeliveryId() { return deliveryId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getStatus() { return status; }
}
