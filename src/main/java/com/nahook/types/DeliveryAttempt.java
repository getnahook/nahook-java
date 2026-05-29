package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single delivery attempt. Returned by {@code mgmt.deliveries().getAttempts()}.
 *
 * <p>The {@link #getStatus()} field is an opaque free-form string emitted by the
 * worker (e.g. {@code "success"}, {@code "failed"}). Do not model it as an enum.
 *
 * <p>Nullable getters: {@link #getResponseStatusCode()}, {@link #getResponseTimeMs()},
 * and {@link #getErrorMessage()} may be {@code null}.
 */
public class DeliveryAttempt {
    private final String id;
    private final int attemptNumber;
    private final String status;
    private final Integer responseStatusCode;
    private final Integer responseTimeMs;
    private final String errorMessage;
    private final String createdAt;

    @JsonCreator
    public DeliveryAttempt(
            @JsonProperty("id") String id,
            @JsonProperty("attemptNumber") int attemptNumber,
            @JsonProperty("status") String status,
            @JsonProperty("responseStatusCode") Integer responseStatusCode,
            @JsonProperty("responseTimeMs") Integer responseTimeMs,
            @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("createdAt") String createdAt) {
        this.id = id;
        this.attemptNumber = attemptNumber;
        this.status = status;
        this.responseStatusCode = responseStatusCode;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public int getAttemptNumber() { return attemptNumber; }
    public String getStatus() { return status; }
    /** Nullable. */
    public Integer getResponseStatusCode() { return responseStatusCode; }
    /** Nullable. */
    public Integer getResponseTimeMs() { return responseTimeMs; }
    /** Nullable. */
    public String getErrorMessage() { return errorMessage; }
    public String getCreatedAt() { return createdAt; }
}
