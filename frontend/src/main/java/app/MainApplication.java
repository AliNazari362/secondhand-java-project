package app;

import javafx.application.Application;
import javafx.stage.Stage;
import atlantafx.base.theme.PrimerLight;
import utils.Pages;
import utils.SceneManager;


public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {

        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        SceneManager.init(stage);
        SceneManager.showPage(Pages.LOGIN, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}