package utils;

import config.DataReceiver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Manages scene navigation and page switching throughout the application.
 * Handles FXML loading, data passing between controllers, and font/stylesheet application.
 */
public class SceneManager {

    private static Stage primaryStage;

    /**
     * Initializes the SceneManager with the primary application stage.
     *
     * @param stage the main application window stage
     */
    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Navigates to a page using its default title and no additional data.
     *
     * @param page  the page to navigate to
     * @param title the window title (uses page default if null)
     */
    public static void showPage(Pages page, String title) {
        showPage(page, title, null);
    }

    /**
     * Navigates to a page with a custom title and optional data to pass to the controller.
     * Loads the FXML file, applies stylesheets and fonts, and transfers data to the controller
     * if it implements the DataReceiver interface.
     *
     * @param page  the page to navigate to
     * @param title the window title (uses page default if null)
     * @param data  optional data object to pass to the target controller
     */
    public static void showPage(Pages page, String title, Object data) {
        String finalTitle = title == null ? page.getTitle() : title;
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + page.getRoot() + ".fxml"));
            Parent root = loader.load();

            if (data != null) {
                Object controller = loader.getController();
                if (controller instanceof DataReceiver receiver) {
                    receiver.receiveData(data);
                }
            }

            Scene scene = new Scene(root, 800, 600);

            scene.getStylesheets().add(
                    Objects.requireNonNull(SceneManager.class.getResource("/css/app.css")).toExternalForm()
            );

            Font font = Font.loadFont(SceneManager.class.getResourceAsStream("/fonts/Vazirmatn.ttf"), 14);
            if (font != null) root.setStyle("-fx-font-family: '" + font.getFamily() + "'; -fx-font-size: 14px;");

            primaryStage.setTitle(finalTitle);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            AlertUtil.showError("خطا در بارگذاری صفحه " + finalTitle);
        }
    }
}