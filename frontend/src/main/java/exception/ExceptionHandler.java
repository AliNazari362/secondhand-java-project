package exception;

import utils.AlertUtil;

public class ExceptionHandler {

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
        e.printStackTrace();
    }

    public static void executeWithHandling(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            handle(e);
        }
    }

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