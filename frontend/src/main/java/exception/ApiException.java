package exception;

/**
 * خطاهای مربوط به پاسخ‌های ناموفق از Backend (کدهای HTTP >= 400).
 */
public class ApiException extends RuntimeException {
    private final int statusCode;

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "statusCode=" + statusCode +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}