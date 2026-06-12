package com.nahook.types;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A JSON field that serializes as either a number or an explicit null.
 *
 * <p>PATCH fields typed {@code NullableInteger} are tri-state: leaving the
 * field unset (a {@code null} reference) omits it from the request body
 * entirely (leave unchanged), {@link #ofNull()} serializes as JSON null
 * (clear), and {@link #of(int)} serializes as the number (set).
 */
public final class NullableInteger {
    private final Integer value;

    private NullableInteger(Integer value) {
        this.value = value;
    }

    /** Returns a {@code NullableInteger} carrying {@code value}. */
    public static NullableInteger of(int value) {
        return new NullableInteger(value);
    }

    /**
     * Returns a {@code NullableInteger} that serializes as explicit JSON
     * null — on {@code UpdateApplicationOptions#maxEndpoints} this clears
     * the cap (unlimited).
     */
    public static NullableInteger ofNull() {
        return new NullableInteger(null);
    }

    @JsonValue
    public Integer value() {
        return value;
    }
}
