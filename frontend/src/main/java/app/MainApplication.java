package app;

import config.ThemeManager;
import utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {
        // اعمال تم AtlantaFX
        ThemeManager.applyTheme();

        // مقداردهی SceneManager
        SceneManager.init(stage);
        SceneManager.showLoginPage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}