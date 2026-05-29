package com.nahook;

import com.nahook.resources.*;
import com.nahook.types.ClientOptions;

import java.time.Duration;

/**
 * Client for the Nahook management API.
 * Management clients do not support retries.
 */
public final class NahookManagement {

    private final EndpointsResource endpoints;
    private final EventTypesResource eventTypes;
    private final ApplicationsResource applications;
    private final SubscriptionsResource subscriptions;
    private final PortalSessionsResource portalSessions;
    private final EnvironmentsResource environments;
    private final DeliveriesResource deliveries;

    /**
     * Create a management client with default options.
     *
     * @param token Management token starting with {@code nhm_}
     * @throws IllegalArgumentException if the token prefix is invalid
     */
    public NahookManagement(String token) {
        this(token, (ClientOptions) null);
    }

    /**
     * Create a management client with custom options.
     * The {@code retries} option is ignored for management clients.
     */
    public NahookManagement(String token, ClientOptions options) {
        if (token == null || !token.startsWith("nhm_")) {
            throw new IllegalArgumentException("Invalid management token: must start with 'nhm_'");
        }
        HttpClientWrapper http = new HttpClientWrapper(
                token,
                options != null ? options.getBaseUrl() : null,
                options != null ? options.getTimeout() : null,
                null // no retries for management
        );
        this.endpoints = new EndpointsResource(http);
        this.eventTypes = new EventTypesResource(http);
        this.applications = new ApplicationsResource(http);
        this.subscriptions = new SubscriptionsResource(http);
        this.portalSessions = new PortalSessionsResource(http);
        this.environments = new EnvironmentsResource(http);
        this.deliveries = new DeliveriesResource(http);
    }

    private NahookManagement(Builder builder) {
        if (builder.token == null || !builder.token.startsWith("nhm_")) {
            throw new IllegalArgumentException("Invalid management token: must start with 'nhm_'");
        }
        HttpClientWrapper http = new HttpClientWrapper(builder.token, builder.baseUrl, builder.timeout, null);
        this.endpoints = new EndpointsResource(http);
        this.eventTypes = new EventTypesResource(http);
        this.applications = new ApplicationsResource(http);
        this.subscriptions = new SubscriptionsResource(http);
        this.portalSessions = new PortalSessionsResource(http);
        this.environments = new EnvironmentsResource(http);
        this.deliveries = new DeliveriesResource(http);
    }

    public static Builder builder(String token) { return new Builder(token); }

    public EndpointsResource endpoints() { return endpoints; }
    public EventTypesResource eventTypes() { return eventTypes; }
    public ApplicationsResource applications() { return applications; }
    public SubscriptionsResource subscriptions() { return subscriptions; }
    public PortalSessionsResource portalSessions() { return portalSessions; }
    public EnvironmentsResource environments() { return environments; }
    public DeliveriesResource deliveries() { return deliveries; }

    public static class Builder {
        private final String token;
        private String baseUrl;
        private Duration timeout;

        private Builder(String token) { this.token = token; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder timeout(Duration timeout) { this.timeout = timeout; return this; }
        public NahookManagement build() { return new NahookManagement(this); }
    }
}
