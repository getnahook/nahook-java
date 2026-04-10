package com.nahook.errors;

/**
 * Request timed out.
 */
public class NahookTimeoutError extends NahookError {

    private final long timeoutMs;

    public NahookTimeoutError(long timeoutMs) {
        super("Request timed out after " + timeoutMs + "ms");
        this.timeoutMs = timeoutMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }
}
