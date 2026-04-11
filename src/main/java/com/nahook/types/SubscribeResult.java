package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscribeResult {
    private final int subscribed;

    @JsonCreator
    public SubscribeResult(@JsonProperty("subscribed") int subscribed) {
        this.subscribed = subscribed;
    }

    public int getSubscribed() { return subscribed; }
}
