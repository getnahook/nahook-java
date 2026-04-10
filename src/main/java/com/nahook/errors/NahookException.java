package com.nahook.errors;

/**
 * Base exception for all Nahook SDK exceptions.
 */
public class NahookException extends RuntimeException {

    public NahookException(String message) {
        super(message);
    }

    public NahookException(String message, Throwable cause) {
        super(message, cause);
    }
}
