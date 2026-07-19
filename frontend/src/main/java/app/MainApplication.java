package app;

import javafx.application.Application;
import javafx.stage.Stage;
import utils.SceneManager;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;


public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {

        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        SceneManager.init(stage);
        SceneManager.showLoginPage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}