package exception;

import model.response.ErrorResponseDto;
import service.ApiClient;
import utils.AlertUtil;

/**
 * Centralized exception handler for the application.
 * Detects the exception type and displays an appropriate message using AlertUtil.
 */
public class ExceptionHandler {

    /**
     * Identifies the exception type and shows a corresponding error alert.
     * Prints the stack trace for debugging purposes.
     *
     * @param e the exception to handle
     */
    public static void handle(Exception e) {
        if (e instanceof ApiException apiEx) {
            ErrorResponseDto error = ApiClient.getInstance().fromJson(apiEx.getMessage(), ErrorResponseDto.class);
            AlertUtil.showError("خطا: " + error.getMessage());
        } else if (e instanceof AuthenticationException) {
            AlertUtil.showError("خطای احراز هویت: " + e.getMessage());
        } else {
            AlertUtil.showError("خطای ناشناخته: " + e.getMessage());
        }
    }
}