package com.nahook.errors;

/**
 * API returned an error response (4xx/5xx).
 */
public class NahookApiException extends NahookException {

    private final int status;
    private final String code;
    private final Integer retryAfter;

    public NahookApiException(int status, String code, String message, Integer retryAfter) {
        super(message);
        this.status = status;
        this.code = code;
        this.retryAfter = retryAfter;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    /**
     * Retry-After header value in seconds, or null if not present.
     */
    public Integer getRetryAfter() {
        return retryAfter;
    }

    /**
     * Whether this error is retryable (5xx or 429).
     */
    public boolean isRetryable() {
        return status >= 500 || status == 429;
    }

    /**
     * Whether this is an authentication error (401 or 403 with token_disabled code).
     */
    public boolean isAuthError() {
        return status == 401 || (status == 403 && "token_disabled".equals(code));
    }

    /**
     * Whether this is a not-found error (404).
     */
    public boolean isNotFound() {
        return status == 404;
    }

    /**
     * Whether this is a rate-limit error (429).
     */
    public boolean isRateLimited() {
        return status == 429;
    }

    /**
     * Whether this is a validation error (400).
     */
    public boolean isValidationError() {
        return status == 400;
    }
}
