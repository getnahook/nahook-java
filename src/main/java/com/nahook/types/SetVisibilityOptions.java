package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetVisibilityOptions {
    @JsonProperty("published") private final boolean published;

    public SetVisibilityOptions(boolean published) {
        this.published = published;
    }
}
