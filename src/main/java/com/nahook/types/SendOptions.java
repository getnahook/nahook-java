package com.nahook.types;

import java.util.Map;

public class SendOptions {

    private final Map<String, Object> payload;
    private final String idempotencyKey;

    public SendOptions(Map<String, Object> payload) {
        this(payload, null);
    }

    public SendOptions(Map<String, Object> payload, String idempotencyKey) {
        this.payload = payload;
        this.idempotencyKey = idempotencyKey;
    }

    public Map<String, Object> getPayload() { return payload; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
