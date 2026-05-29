package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A delivery record returned by {@code mgmt.deliveries().get()}. Carries the same
 * metadata fields as {@link Delivery} plus an optional {@link PayloadEnvelope}
 * that is populated only when the call passes
 * {@code GetDeliveryOptions.builder().includePayload(true).build()}.
 *
 * <p>When {@code includePayload} is omitted, {@link #getPayload()} returns {@code null}.
 */
public class DeliveryWithPayload {
    private final String id;
    private final String idempotencyKey;
    private final String endpointId;
    private final String status;
    private final int totalAttempts;
    private final String firstAttemptAt;
    private final String deliveredAt;
    private final String nextRetryAt;
    private final boolean hasPayload;
    private final String createdAt;
    private final String updatedAt;
    private final PayloadEnvelope payload;

    @JsonCreator
    public DeliveryWithPayload(
            @JsonProperty("id") String id,
            @JsonProperty("idempotencyKey") String idempotencyKey,
            @JsonProperty("endpointId") String endpointId,
            @JsonProperty("status") String status,
            @JsonProperty("totalAttempts") int totalAttempts,
            @JsonProperty("firstAttemptAt") String firstAttemptAt,
            @JsonProperty("deliveredAt") String deliveredAt,
            @JsonProperty("nextRetryAt") String nextRetryAt,
            @JsonProperty("hasPayload") boolean hasPayload,
            @JsonProperty("createdAt") String createdAt,
            @JsonProperty("updatedAt") String updatedAt,
            @JsonProperty("payload") PayloadEnvelope payload) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.endpointId = endpointId;
        this.status = status;
        this.totalAttempts = totalAttempts;
        this.firstAttemptAt = firstAttemptAt;
        this.deliveredAt = deliveredAt;
        this.nextRetryAt = nextRetryAt;
        this.hasPayload = hasPayload;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.payload = payload;
    }

    public String getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getEndpointId() { return endpointId; }
    /** One of: {@code pending}, {@code delivering}, {@code delivered}, {@code scheduled_retry}, {@code failed}, {@code dead_letter}. */
    public String getStatus() { return status; }
    public int getTotalAttempts() { return totalAttempts; }
    /** Nullable. */
    public String getFirstAttemptAt() { return firstAttemptAt; }
    /** Nullable. */
    public String getDeliveredAt() { return deliveredAt; }
    /** Nullable. */
    public String getNextRetryAt() { return nextRetryAt; }
    public boolean hasPayload() { return hasPayload; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    /** Non-null only when the request included {@code includePayload=true}. */
    public PayloadEnvelope getPayload() { return payload; }
}
