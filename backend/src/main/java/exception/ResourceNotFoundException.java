package exception;

/**
 * زمانی که منبعی (کاربر، آگهی، ...) پیدا نشود.
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " با شناسه " + id + " یافت نشد.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}