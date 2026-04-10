package com.nahook.errors;

/**
 * Network-level failure (no HTTP response received).
 */
public class NahookNetworkError extends NahookError {

    public NahookNetworkError(Throwable cause) {
        super("Network error: " + cause.getMessage(), cause);
    }
}
