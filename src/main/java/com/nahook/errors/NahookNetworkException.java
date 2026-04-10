package com.nahook.errors;

/**
 * Network-level failure (no HTTP response received).
 */
public class NahookNetworkException extends NahookException {

    public NahookNetworkException(Throwable cause) {
        super("Network error: " + cause.getMessage(), cause);
    }
}
