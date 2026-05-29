package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Tagged envelope carrying the original webhook payload (or the reason it isn't
 * available). Returned inside {@link DeliveryWithPayload#getPayload()} when
 * {@code includePayload=true} is passed to
 * {@code mgmt.deliveries().get(workspaceId, deliveryId, options)}.
 *
 * <p>The discriminator is {@link #getStatus()}, which is one of:
 * <ul>
 *   <li>{@code "available"} — {@link #getData()} and {@link #getContentType()} are non-null.</li>
 *   <li>{@code "forbidden"} — workspace plan does not include payload storage.</li>
 *   <li>{@code "processing"} — delivery still in flight, payload write may be racing the read.</li>
 *   <li>{@code "not_found"} — terminal delivery without stored payload (older row or plan was lower at ingest).</li>
 *   <li>{@code "error"} — transient infrastructure failure.</li>
 * </ul>
 *
 * <p>For the {@code "available"} status, {@code data} is the original webhook
 * body as a {@link JsonNode}; convert with
 * {@code MAPPER.treeToValue(envelope.getData(), YourType.class)} or
 * {@code envelope.getData().toString()} for raw JSON.
 */
public class PayloadEnvelope {
    private final String status;
    private final JsonNode data;
    private final String contentType;

    @JsonCreator
    public PayloadEnvelope(
            @JsonProperty("status") String status,
            @JsonProperty("data") JsonNode data,
            @JsonProperty("contentType") String contentType) {
        this.status = status;
        this.data = data;
        this.contentType = contentType;
    }

    /** Discriminator: {@code available}, {@code forbidden}, {@code processing}, {@code not_found}, or {@code error}. */
    public String getStatus() { return status; }

    /** Non-null only when {@link #getStatus()} is {@code "available"}. */
    public JsonNode getData() { return data; }

    /** Non-null only when {@link #getStatus()} is {@code "available"} (always {@code "application/json"} in v1). */
    public String getContentType() { return contentType; }
}
