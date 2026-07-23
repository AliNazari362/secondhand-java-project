package utils;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Utility class for displaying graphical dialogs and alerts in JavaFX.
 * Provides methods for error, success, warning, confirmation, and text input dialogs.
 * All dialogs are automatically configured with Right-to-Left (RTL) orientation.
 */
public class AlertUtil {

    /**
     * Displays a non-blocking error alert dialog.
     *
     * @param message the error message to display
     */
    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "خطا", message);
    }

    /**
     * Displays a non-blocking success/information alert dialog.
     *
     * @param message the success message to display
     */
    public static void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "موفق", message);
    }

    /**
     * Displays a non-blocking warning alert dialog.
     *
     * @param message the warning message to display
     */
    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "توجه", message);
    }

    /**
     * Displays an alert dialog without blocking the calling thread.
     * If called from a non-UI thread, the alert is delegated to the JavaFX Application Thread.
     *
     * @param type    the type of alert to display
     * @param title   the title of the alert window
     * @param message the content message
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        if (Platform.isFxApplicationThread()) {
            createAndShowAlert(type, title, message);
        } else {
            Platform.runLater(() -> createAndShowAlert(type, title, message));
        }
    }

    /**
     * Creates and immediately shows an alert dialog on the JavaFX Application Thread.
     *
     * @param type    the type of alert to display
     * @param title   the title of the alert window
     * @param message the content message
     */
    private static void createAndShowAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        applyRTL(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.NONE);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(false);
        alert.show();
    }

    /**
     * Displays a blocking confirmation dialog with OK and Cancel buttons.
     *
     * @param title   the header title of the confirmation dialog
     * @param message the content message to display
     * @return true if the user clicks OK, false otherwise
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
     * Displays a text input dialog for collecting user input.
     *
     * @param title        the title of the dialog window
     * @param message      the prompt message for the input field
     * @param defaultValue the default text to pre-fill (can be null)
     * @return the text entered by the user, or null if the dialog is canceled
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
     * Applies Right-to-Left (RTL) orientation to an Alert dialog pane.
     * Ensures correct layout and button positioning for Persian text,
     * even when the title or content contains English words.
     *
     * @param alert the Alert dialog to apply RTL orientation to
     */
    private static void applyRTL(Alert alert) {
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }

    /**
     * Applies Right-to-Left (RTL) orientation to a TextInputDialog pane.
     *
     * @param dialog the TextInputDialog to apply RTL orientation to
     */
    private static void applyRTL(TextInputDialog dialog) {
        dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }
}