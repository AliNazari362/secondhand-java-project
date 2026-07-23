package utils;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Utility class for displaying graphical dialogs and alerts in JavaFX.
 * Provides methods for error, success, warning, confirmation, and text input dialogs.
 * All dialogs are automatically configured with Right-to-Left (RTL) orientation.
 */
public class AlertUtil {

    /**
     * Displays an error alert to the user.
     *
     * @param message the error message to be shown.
     */
    public static void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("خطا");
            alert.setHeaderText(null);
            alert.setContentText(message);
            applyRTL(alert);
            alert.show();
        });
    }

    /**
     * Displays a success (information) alert to the user.
     *
     * @param message the success message to be shown.
     */
    public static void showSuccess(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("موفق");
            alert.setHeaderText(null);
            alert.setContentText(message);
            applyRTL(alert);
            alert.show();
        });
    }

    /**
     * Displays a warning alert to the user.
     *
     * @param message the warning message to be shown.
     */
    public static void showWarning(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("توجه");
            alert.setHeaderText(null);
            alert.setContentText(message);
            applyRTL(alert);
            alert.show();
        });
    }

    /**
     * Displays a confirmation dialog with OK and Cancel buttons.
     *
     * @param title   the header title of the confirmation dialog.
     * @param message the content message to be shown.
     * @return true if the user clicks OK, false otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأیید");
        alert.setHeaderText(title);
        alert.setContentText(message);
        applyRTL(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Displays a text input dialog for user input.
     *
     * @param title        the title of the dialog window.
     * @param message      the prompt message for the input field.
     * @param defaultValue the default text to pre-fill (can be null).
     * @return the text entered by the user, or null if the dialog is canceled.
     */
    public static String showInputDialog(String title, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        applyRTL(dialog);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Applies Right-to-Left (RTL) orientation to the dialog pane.
     * This ensures correct layout and button positioning for Persian text,
     * even when the title or content contains English words.
     *
     * @param alert the Alert or TextInputDialog to apply RTL to.
     */
    private static void applyRTL(Alert alert) {
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }

    /**
     * Applies Right-to-Left (RTL) orientation to the text input dialog pane.
     *
     * @param dialog the TextInputDialog to apply RTL to.
     */
    private static void applyRTL(TextInputDialog dialog) {
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }
}