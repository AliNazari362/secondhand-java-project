package com.secondhand.exception;

import java.time.LocalDateTime;

/**
 * Standardised error response body returned by {@link GlobalExceptionHandler} for all
 * error conditions.
 *
 * <p>Each instance captures the HTTP status code, a human-readable message, and the
 * exact timestamp at which the error occurred.</p>
 */
public class ErrorResponse {

    /** The HTTP status code associated with this error (e.g., 400, 404, 500). */
    private final int status;

    /** The UTC timestamp at which this error response was created. */
    private final LocalDateTime timestamp;

    /** A human-readable description of the error. */
    private final String message;

    /**
     * Constructs a new {@code ErrorResponse} with the given status code and message.
     * The {@code timestamp} is set to {@link LocalDateTime#now()} at construction time.
     *
     * @param status  the HTTP status code
     * @param message a descriptive error message to return to the client
     */
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    /**
     * Returns the HTTP status code for this error response.
     *
     * @return the HTTP status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the timestamp at which this error response was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the human-readable error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }
}