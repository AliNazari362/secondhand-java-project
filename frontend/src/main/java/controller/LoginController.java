package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.request.LoginRequest;
import model.response.LoginResponse;
import service.AuthService;
import utils.*;

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
            SessionManager.setSession(
                    response.getToken(),
                    response.getUserId(),
                    response.getFullName(),
                    response.getRole().name()
            );

            // ✅ تغییر اصلی: به صفحه داشبورد بروید که هدر را دارد
            SceneManager.showPage(Pages.DASHBOARD, null);

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