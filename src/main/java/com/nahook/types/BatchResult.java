package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BatchResult {

    private final List<BatchResultItem> items;

    @JsonCreator
    public BatchResult(@JsonProperty("items") List<BatchResultItem> items) {
        this.items = items;
    }

    public List<BatchResultItem> getItems() { return items; }
}
