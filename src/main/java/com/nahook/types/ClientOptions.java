package com.nahook.types;

import java.time.Duration;

/**
 * Configuration options for NahookClient and NahookManagement.
 */
public class ClientOptions {

    private final String baseUrl;
    private final Duration timeout;
    private final Integer retries;

    private ClientOptions(String baseUrl, Duration timeout, Integer retries) {
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.retries = retries;
    }

    public String getBaseUrl() { return baseUrl; }
    public Duration getTimeout() { return timeout; }
    public Integer getRetries() { return retries; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String baseUrl;
        private Duration timeout;
        private Integer retries;

        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder timeout(Duration timeout) { this.timeout = timeout; return this; }
        public Builder retries(int retries) { this.retries = retries; return this; }
        public ClientOptions build() { return new ClientOptions(baseUrl, timeout, retries); }
    }
}
