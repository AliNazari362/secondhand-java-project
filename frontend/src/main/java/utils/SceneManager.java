package utils;

import config.DataReceiver;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showPage(Pages page, String title) {
        showPage(page, title, null);
    }

    public static void showPage(Pages page, String title, Object data) {
        String finalTitle = title == null ? page.getTitle() : title;
        try {
            // Set fxml File
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + page.getRoot() + ".fxml"));
            Parent root = loader.load();

            // Transfer data to controller
            if (data != null) {
                Object controller = loader.getController();
                if (controller instanceof DataReceiver receiver) {
                    receiver.receiveData(data);
                }
            }

            Scene scene = new Scene(root, 800, 600);

            // Set Style
            scene.getStylesheets().add(
                    Objects.requireNonNull(SceneManager.class.getResource("/css/app.css")).toExternalForm()
            );

            // Set font
            Font font = Font.loadFont(SceneManager.class.getResourceAsStream("/fonts/anjoman.ttf"), 14);
            if (font != null) root.setStyle("-fx-font-family: '" + font.getFamily() + "'; -fx-font-size: 14px;");

            primaryStage.setTitle(finalTitle);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("خطا در بارگذاری صفحه " + finalTitle);
        }
    }
}