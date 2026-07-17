package app;

import utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * کلاس اصلی برنامه JavaFX.
 * نقطه ورود برنامه از اینجا شروع می‌شود.
 */
public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // مقداردهی اولیه SceneManager با Stage اصلی
        SceneManager.init(primaryStage);

        // نمایش صفحه ورود به عنوان اولین صفحه
        SceneManager.showLoginPage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}