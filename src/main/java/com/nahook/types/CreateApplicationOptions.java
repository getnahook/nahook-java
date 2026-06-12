package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateApplicationOptions {
    @JsonProperty("name") private final String name;
    @JsonProperty("externalId") private final String externalId;
    @JsonProperty("metadata") private final Map<String, String> metadata;
    @JsonProperty("maxEndpoints") private final Integer maxEndpoints;
    @JsonProperty("showEventTypes") private final Boolean showEventTypes;

    public CreateApplicationOptions(String name) { this(name, null, null); }
    public CreateApplicationOptions(String name, String externalId, Map<String, String> metadata) {
        this(name, externalId, metadata, null, null);
    }
    public CreateApplicationOptions(String name, String externalId, Map<String, String> metadata,
                                    Integer maxEndpoints, Boolean showEventTypes) {
        this.name = name; this.externalId = externalId; this.metadata = metadata;
        this.maxEndpoints = maxEndpoints; this.showEventTypes = showEventTypes;
    }

    public static Builder builder(String name) { return new Builder(name); }

    public static class Builder {
        private final String name;
        private String externalId;
        private Map<String, String> metadata;
        private Integer maxEndpoints;
        private Boolean showEventTypes;

        private Builder(String name) { this.name = name; }

        public Builder externalId(String externalId) { this.externalId = externalId; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        /**
         * Caps how many endpoints this application may have (disabled
         * endpoints count). 0 makes the application read-only; not calling
         * this means unlimited.
         */
        public Builder maxEndpoints(int maxEndpoints) { this.maxEndpoints = maxEndpoints; return this; }

        /** Whether the Developer Portal exposes the event-type catalog. Server defaults to true. */
        public Builder showEventTypes(boolean showEventTypes) { this.showEventTypes = showEventTypes; return this; }

        public CreateApplicationOptions build() {
            return new CreateApplicationOptions(name, externalId, metadata, maxEndpoints, showEventTypes);
        }
    }
}
