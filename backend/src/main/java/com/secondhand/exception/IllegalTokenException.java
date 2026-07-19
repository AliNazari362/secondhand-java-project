package com.secondhand.exception;

/**
 * Exception thrown when a JWT token provided in the {@code Authorization} header is
 * missing, malformed, expired, or otherwise invalid.
 *
 * <p>This exception is mapped to HTTP 401 Unauthorized by {@link GlobalExceptionHandler}.</p>
 */
public class IllegalTokenException extends RuntimeException {

    /**
     * Constructs a new {@code IllegalTokenException} with the specified detail message.
     *
     * @param message a human-readable description of the token validation failure
     */
    public IllegalTokenException(String message) {
        super(message);
    }
}
