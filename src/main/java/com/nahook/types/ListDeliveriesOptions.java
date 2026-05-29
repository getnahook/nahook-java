package com.nahook.types;

/**
 * Optional filters for {@code mgmt.deliveries().list()}.
 *
 * <p>All fields are nullable — unset fields are omitted from the query string.
 *
 * <ul>
 *   <li>{@code limit} — page size. Server default is 50, max 100.</li>
 *   <li>{@code cursor} — opaque encrypted token from a previous response.
 *       Pass {@link PaginatedResult#getNextCursor()} verbatim; do not decode it.</li>
 *   <li>{@code status} — one of: {@code pending}, {@code delivering},
 *       {@code delivered}, {@code scheduled_retry}, {@code failed}, {@code dead_letter}.</li>
 * </ul>
 */
public class ListDeliveriesOptions {
    private final Integer limit;
    private final String cursor;
    private final String status;

    public ListDeliveriesOptions(Integer limit, String cursor, String status) {
        this.limit = limit;
        this.cursor = cursor;
        this.status = status;
    }

    public Integer getLimit() { return limit; }
    public String getCursor() { return cursor; }
    public String getStatus() { return status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer limit;
        private String cursor;
        private String status;

        public Builder limit(Integer limit) { this.limit = limit; return this; }
        public Builder cursor(String cursor) { this.cursor = cursor; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public ListDeliveriesOptions build() { return new ListDeliveriesOptions(limit, cursor, status); }
    }
}
