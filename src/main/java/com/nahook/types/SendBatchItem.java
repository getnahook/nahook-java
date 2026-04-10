package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendBatchItem {

    @JsonProperty("endpointId")
    private final String endpointId;

    @JsonProperty("payload")
    private final Map<String, Object> payload;

    @JsonProperty("idempotencyKey")
    private final String idempotencyKey;

    public SendBatchItem(String endpointId, Map<String, Object> payload) {
        this(endpointId, payload, null);
    }

    public SendBatchItem(String endpointId, Map<String, Object> payload, String idempotencyKey) {
        this.endpointId = endpointId;
        this.payload = payload;
        this.idempotencyKey = idempotencyKey;
    }

    public String getEndpointId() { return endpointId; }
    public Map<String, Object> getPayload() { return payload; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
