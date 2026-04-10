package com.nahook.types;

import java.util.Map;

public class TriggerOptions {

    private final Map<String, Object> payload;
    private final Map<String, String> metadata;

    public TriggerOptions(Map<String, Object> payload) {
        this(payload, null);
    }

    public TriggerOptions(Map<String, Object> payload, Map<String, String> metadata) {
        this.payload = payload;
        this.metadata = metadata;
    }

    public Map<String, Object> getPayload() { return payload; }
    public Map<String, String> getMetadata() { return metadata; }
}
