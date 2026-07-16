package exception;

import utils.AlertUtil;

/**
 * مدیریت متمرکز خطاها در سراسر برنامه.
 * تشخیص نوع استثنا و نمایش پیام مناسب با AlertUtil.
 */
public class ExceptionHandler {

    /**
     * تشخیص نوع استثنا و نمایش پیام مناسب.
     */
    public static void handle(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiEx = (ApiException) e;
            AlertUtil.showError("خطای سرور (" + apiEx.getStatusCode() + "): " + apiEx.getMessage());
        } else if (e instanceof AuthenticationException) {
            AlertUtil.showError("خطای احراز هویت: " + e.getMessage());
        } else if (e instanceof BusinessException) {
            AlertUtil.showError("خطای منطقی: " + e.getMessage());
        } else {
            AlertUtil.showError("خطای ناشناخته: " + e.getMessage());
        }
        // چاپ خطا برای دیباگ (اختیاری)
        e.printStackTrace();
    }

    /**
     * اجرای یک قطعه کد و مدیریت خودکار خطاهای آن.
     * این متد برای مواقعی که نمی‌خواهید try-catch بنویسید، مفید است.
     *
     * @param action قطعه کدی که ممکن است خطا پرتاب کند.
     */
    public static void executeWithHandling(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            handle(e);
        }
    }

    /**
     * اجرای یک قطعه کد و در صورت موفقیت، اجرای callback.
     *
     * @param action    قطعه کد اصلی
     * @param onSuccess در صورت عدم وجود خطا اجرا می‌شود
     */
    public static void executeWithHandling(Runnable action, Runnable onSuccess) {
        try {
            action.run();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            handle(e);
        }
    }
}