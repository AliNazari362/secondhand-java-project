package exception;

/**
 * زمانی که کاربر عادی تلاش به انجام عملیات مدیریتی کند.
 */
public class AdminPermissionException extends BusinessException {
    public AdminPermissionException(String message) {
        super(message);
    }

    public AdminPermissionException() {
        super("شما دسترسی ادمین برای این بخش ندارید");
    }
}