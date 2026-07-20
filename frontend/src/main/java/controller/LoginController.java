package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.request.LoginRequest;
import model.response.LoginResponse;
import service.AuthService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.ValidationUtil;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        try {
            String email = emailField.getText();
            String passwordPlain = passwordField.getText();

            ValidationUtil.isValidEmail(email);
            ValidationUtil.isValidPassword(passwordPlain);

            LoginRequest request = new LoginRequest(email, passwordPlain);
            LoginResponse response = AuthService.login(request);
            SceneManager.showPage(Pages.LIST_ADS, null);

            Platform.runLater(() -> AlertUtil.showSuccess(response.getFullName() + " عزیز خوش آمدید"));
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    @FXML
    public void goToRegister() {
        SceneManager.showPage(Pages.REGISTER, null);
    }
}