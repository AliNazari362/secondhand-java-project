package com.secondhand.exception;

/**
 * Exception thrown when the client sends a request that contains invalid or malformed data.
 *
 * <p>This exception is mapped to HTTP 400 Bad Request by {@link GlobalExceptionHandler}.</p>
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new {@code BadRequestException} with the specified detail message.
     *
     * @param message a human-readable description of the validation or input error
     */
    public BadRequestException(String message) {
        super(message);
    }
}
