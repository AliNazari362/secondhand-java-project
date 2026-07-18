package com.secondhand.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 *
 * <p>Intercepts exceptions thrown by any controller and maps them to structured
 * {@link ErrorResponse} bodies with the appropriate HTTP status codes.
 * Uses {@link RestControllerAdvice} so it applies across all {@code @RestController} beans.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResourceNotFoundException} thrown when a requested resource does not exist.
     *
     * @param e the exception carrying the not-found message
     * @return an {@link ErrorResponse} with HTTP 404 and the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException e) {
        return new ErrorResponse(404, e.getMessage());
    }

    /**
     * Handles {@link ResourceAlreadyExistsException} thrown when attempting to create a resource
     * that already exists.
     *
     * @param e the exception carrying the conflict message
     * @return an {@link ErrorResponse} with HTTP 409 and the exception message
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExists(ResourceAlreadyExistsException e) {
        return new ErrorResponse(409, e.getMessage());
    }

    /**
     * Handles {@link ForbiddenException} thrown when the authenticated user lacks the required
     * permission to perform an action.
     *
     * @param e the exception carrying the forbidden message
     * @return an {@link ErrorResponse} with HTTP 403 and the exception message
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException e) {
        return new ErrorResponse(403, e.getMessage());
    }

    /**
     * Handles {@link BadRequestException} thrown when the request contains invalid data
     * that fails application-level validation.
     *
     * @param e the exception carrying the bad-request message
     * @return an {@link ErrorResponse} with HTTP 400 and the exception message
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException e) {
        return new ErrorResponse(400, e.getMessage());
    }

    /**
     * Handles {@link IllegalTokenException} thrown when the provided JWT token is missing,
     * malformed, or expired.
     *
     * @param e the exception carrying the unauthorized message
     * @return an {@link ErrorResponse} with HTTP 401 and the exception message
     */
    @ExceptionHandler(IllegalTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleIllegalToken(IllegalTokenException e) {
        return new ErrorResponse(401, e.getMessage());
    }

    /**
     * Handles {@link MethodArgumentNotValidException} thrown by Spring when a request body
     * annotated with {@code @Valid} fails Bean Validation constraints.
     *
     * <p>Collects all field-level validation errors and joins them into a single message.</p>
     *
     * @param e the exception containing binding result details for all constraint violations
     * @return an {@link ErrorResponse} with HTTP 400 and a comma-separated list of field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse(400, message);
    }

    /**
     * Handles {@link IllegalArgumentException} thrown when an invalid argument is passed to a
     * service or utility method (e.g., an unrecognised enum value).
     *
     * @param e the exception carrying the illegal-argument message
     * @return an {@link ErrorResponse} with HTTP 400 and a generic invalid-input message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return new ErrorResponse(400, "مقدار ورودی نامعتبر است");
    }

    /**
     * Catch-all handler for any unhandled {@link Exception} not matched by a more specific handler.
     *
     * <p>Logs the error and returns a generic internal-server-error response to avoid leaking
     * sensitive stack-trace information to the client.</p>
     *
     * @param e the unhandled exception
     * @return an {@link ErrorResponse} with HTTP 500 and the exception message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse(500, e.getMessage());
    }
}
