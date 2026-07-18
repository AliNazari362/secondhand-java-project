package exception;

/**
 * خطاهای اعتبارسنجی داده‌های ورودی.
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String field, String reason) {
        super("خطا در فیلد " + field + ": " + reason);
    }
}