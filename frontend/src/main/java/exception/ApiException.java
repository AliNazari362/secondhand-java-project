package exception;

/**
 * استثنای مربوط به خطاهای دریافتی از Backend.
 * شامل پیام خطا و کد وضعیت HTTP است.
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
                ", message=" + getMessage() +
                '}';
    }
}