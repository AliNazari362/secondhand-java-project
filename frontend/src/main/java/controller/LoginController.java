package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private VBox root;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        // TODO: پیاده‌سازی ورود
        System.out.println("Login clicked!");
    }

    @FXML
    public void goToRegister() {
        // TODO: رفتن به صفحه ثبت‌نام
        System.out.println("Go to register clicked!");
    }
}