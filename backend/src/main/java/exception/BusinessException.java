package exception;

/**
 * کلاس پایه برای همه‌ی استثناهای مربوط به منطق کسب‌وکار.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}