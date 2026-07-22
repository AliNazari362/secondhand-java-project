package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.request.UserRegisterRequest;
import model.response.UserDto;
import service.AuthService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.ValidationUtil;

public class RegisterController {

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField phoneField;

    @FXML
    public void handleRegister() {
        try {
            String email = emailField.getText();
            String passwordPlain = passwordField.getText();
            String fullName = fullNameField.getText();
            String phoneNumber = phoneField.getText();

            ValidationUtil.isValidEmail(email);
            ValidationUtil.isValidPassword(passwordPlain);
            ValidationUtil.isNotEmpty(fullName);
            ValidationUtil.isValidPhone(phoneNumber);

            UserRegisterRequest request = new UserRegisterRequest(fullName, email, passwordPlain, phoneNumber);
            UserDto response = AuthService.register(request);
            SceneManager.showPage(Pages.LOGIN, null);

            Platform.runLater(() -> AlertUtil.showSuccess(response.getFullName() + " عزیز ثبت نام با موفقیت انجام شد، حالا وارد شوید"));
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    @FXML
    public void goToLogin() {
        SceneManager.showPage(Pages.LOGIN, null);
    }
}