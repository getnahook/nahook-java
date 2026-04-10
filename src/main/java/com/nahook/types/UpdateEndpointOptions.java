package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEndpointOptions {
    @JsonProperty("url") private final String url;
    @JsonProperty("description") private final String description;
    @JsonProperty("metadata") private final Map<String, String> metadata;
    @JsonProperty("isActive") private final Boolean isActive;

    private UpdateEndpointOptions(Builder b) {
        this.url = b.url; this.description = b.description;
        this.metadata = b.metadata; this.isActive = b.isActive;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String url;
        private String description;
        private Map<String, String> metadata;
        private Boolean isActive;

        public Builder url(String url) { this.url = url; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }
        public Builder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public UpdateEndpointOptions build() { return new UpdateEndpointOptions(this); }
    }
}
