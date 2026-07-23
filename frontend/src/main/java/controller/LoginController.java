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

/**
 * Controller for the login page.
 * Handles user authentication by validating credentials,
 * establishing a session, and navigating to the dashboard on success.
 */
public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    /**
     * Handles the login form submission.
     * Validates email and password, sends a login request to the server,
     * stores the session on success, and navigates to the dashboard.
     */
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

            SceneManager.showPage(Pages.DASHBOARD, response.getFullName());
            Platform.runLater(() -> AlertUtil.showSuccess(response.getFullName() + " عزیز خوش آمدید"));
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Navigates to the registration page.
     */
    @FXML
    public void goToRegister() {
        SceneManager.showPage(Pages.REGISTER, null);
    }
}