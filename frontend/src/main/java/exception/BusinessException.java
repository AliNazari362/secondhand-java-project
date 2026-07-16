package exception;

/**
 * خطاهای مربوط به منطق برنامه (مثلاً اعتبارسنجی ورودی‌ها، خطاهای دامنه).
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}