package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateApplicationOptions {
    @JsonProperty("name") private final String name;
    @JsonProperty("metadata") private final Map<String, String> metadata;
    @JsonProperty("maxEndpoints") private final NullableInteger maxEndpoints;
    @JsonProperty("showEventTypes") private final Boolean showEventTypes;

    public UpdateApplicationOptions(String name, Map<String, String> metadata) {
        this(name, metadata, null, null);
    }

    public UpdateApplicationOptions(String name, Map<String, String> metadata,
                                    NullableInteger maxEndpoints, Boolean showEventTypes) {
        this.name = name; this.metadata = metadata;
        this.maxEndpoints = maxEndpoints; this.showEventTypes = showEventTypes;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private Map<String, String> metadata;
        private NullableInteger maxEndpoints;
        private Boolean showEventTypes;

        public Builder name(String name) { this.name = name; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        /**
         * Sets the endpoint cap (disabled endpoints count; 0 makes the
         * application read-only). Not calling either {@code maxEndpoints}
         * method leaves the current cap unchanged.
         */
        public Builder maxEndpoints(int maxEndpoints) {
            this.maxEndpoints = NullableInteger.of(maxEndpoints); return this;
        }

        /** Clears the endpoint cap (unlimited) by sending an explicit JSON null. */
        public Builder clearMaxEndpoints() {
            this.maxEndpoints = NullableInteger.ofNull(); return this;
        }

        /** Whether the Developer Portal exposes the event-type catalog. Unchanged when not called. */
        public Builder showEventTypes(boolean showEventTypes) {
            this.showEventTypes = showEventTypes; return this;
        }

        public UpdateApplicationOptions build() {
            return new UpdateApplicationOptions(name, metadata, maxEndpoints, showEventTypes);
        }
    }
}
