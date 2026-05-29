package com.nahook.types;

/**
 * Options for {@code mgmt.deliveries().get()}.
 *
 * <p>{@code includePayload=true} adds {@code ?include=payload} to the request and
 * causes the response to include a {@link PayloadEnvelope}. Default is {@code false}.
 */
public class GetDeliveryOptions {
    private final Boolean includePayload;

    public GetDeliveryOptions(Boolean includePayload) {
        this.includePayload = includePayload;
    }

    public Boolean getIncludePayload() { return includePayload; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Boolean includePayload;
        public Builder includePayload(Boolean includePayload) { this.includePayload = includePayload; return this; }
        public GetDeliveryOptions build() { return new GetDeliveryOptions(includePayload); }
    }
}
