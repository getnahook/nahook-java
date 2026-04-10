package com.nahook.errors;

/**
 * Base error for all Nahook SDK errors.
 */
public class NahookError extends RuntimeException {

    public NahookError(String message) {
        super(message);
    }

    public NahookError(String message, Throwable cause) {
        super(message, cause);
    }
}
