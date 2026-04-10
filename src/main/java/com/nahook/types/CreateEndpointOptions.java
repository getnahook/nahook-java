package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateEndpointOptions {
    @JsonProperty("url") private final String url;
    @JsonProperty("type") private final String type;
    @JsonProperty("description") private final String description;
    @JsonProperty("metadata") private final Map<String, String> metadata;
    @JsonProperty("config") private final Map<String, Object> config;
    @JsonProperty("authUsername") private final String authUsername;
    @JsonProperty("authPassword") private final String authPassword;

    private CreateEndpointOptions(Builder b) {
        this.url = b.url; this.type = b.type; this.description = b.description;
        this.metadata = b.metadata; this.config = b.config;
        this.authUsername = b.authUsername; this.authPassword = b.authPassword;
    }

    public static Builder builder(String url) { return new Builder(url); }

    public static class Builder {
        private final String url;
        private String type;
        private String description;
        private Map<String, String> metadata;
        private Map<String, Object> config;
        private String authUsername;
        private String authPassword;

        public Builder(String url) { this.url = url; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }
        public Builder config(Map<String, Object> config) { this.config = config; return this; }
        public Builder authUsername(String authUsername) { this.authUsername = authUsername; return this; }
        public Builder authPassword(String authPassword) { this.authPassword = authPassword; return this; }
        public CreateEndpointOptions build() { return new CreateEndpointOptions(this); }
    }
}
