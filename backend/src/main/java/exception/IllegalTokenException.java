package exception;

/**
 * زمانی که توکن JWT نامعتبر یا منقضی باشد.
 */
public class IllegalTokenException extends BusinessException {
    public IllegalTokenException(String message) {
        super(message);
    }

    public IllegalTokenException() {
        super("توکن نامعتبر است");
    }
}