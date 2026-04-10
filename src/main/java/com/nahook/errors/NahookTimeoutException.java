package com.nahook.errors;

/**
 * Request timed out.
 */
public class NahookTimeoutException extends NahookException {

    private final long timeoutMs;

    public NahookTimeoutException(long timeoutMs, Throwable cause) {
        super("Request timed out after " + timeoutMs + "ms", cause);
        this.timeoutMs = timeoutMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }
}
