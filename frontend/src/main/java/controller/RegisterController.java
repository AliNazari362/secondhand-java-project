package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;

    @FXML
    public void handleRegister() {
        // TODO: پیاده‌سازی ثبت‌نام
        System.out.println("Register clicked!");
    }

    @FXML
    public void goToLogin() {
        // TODO: رفتن به صفحه لاگین
        System.out.println("Go to login clicked!");
    }
}