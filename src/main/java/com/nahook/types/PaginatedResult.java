package com.nahook.types;

import java.util.List;

/**
 * Generic cursor-paginated result returned by management list methods that
 * paginate (e.g. {@code mgmt.deliveries().list()}). Unlike {@link ListResult},
 * which carries a single page of an unpaginated list, this carries an opaque
 * {@code nextCursor} to fetch the next page.
 *
 * <p>{@link #getNextCursor()} is explicitly {@code null} when there are no
 * more pages (not absent / undefined). Pass it back verbatim — it is an
 * opaque encrypted token; do not decode or modify it.
 */
public class PaginatedResult<T> {
    private final List<T> data;
    private final String nextCursor;

    public PaginatedResult(List<T> data, String nextCursor) {
        this.data = data;
        this.nextCursor = nextCursor;
    }

    public List<T> getData() { return data; }

    /** Nullable. {@code null} signals the last page. */
    public String getNextCursor() { return nextCursor; }
}
