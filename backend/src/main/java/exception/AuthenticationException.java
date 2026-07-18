package exception;

/**
 * خطاهای مربوط به احراز هویت (ورود، ثبت‌نام).
 */
public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super("نام کاربری یا رمز عبور اشتباه است");
    }
}