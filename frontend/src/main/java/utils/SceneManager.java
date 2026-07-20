package utils;

import controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, 520, 500);
            // اضافه کردن CSS سفارشی
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/css/app.css").toExternalForm()
            );
            primaryStage.setTitle("ورود به سامانه");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("خطا در بارگذاری صفحه ورود: " + e.getMessage());
        }
    }

    // متدهای دیگر برای صفحات بعدی...
}