package exception;

/**
 * خطاهای مربوط به ورود، ثبت‌نام و احراز هویت.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}