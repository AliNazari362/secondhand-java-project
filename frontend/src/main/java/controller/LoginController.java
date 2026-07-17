package controller;

import exception.ExceptionHandler;
import model.request.LoginRequest;
import model.response.LoginResponse;
import service.ApiClient;
import service.SessionManager;
import utils.AlertUtil;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = createRoot();
        }
        return root;
    }

    private static VBox createRoot() {
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        vbox.setStyle("-fx-background-color: #f5f5f5;");

        // عنوان
        Text title = new Text("ورود به سامانه");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;");

        // فیلد ایمیل
        TextField emailField = new TextField();
        emailField.setPromptText("ایمیل");
        emailField.setMaxWidth(300);
        emailField.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-border-radius: 8;");

        // فیلد رمز عبور
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("رمز عبور");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-border-radius: 8;");

        // دکمه ورود
        Button loginBtn = new Button("ورود");
        loginBtn.setMaxWidth(300);
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;");
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                AlertUtil.showError("لطفاً همه فیلدها را پر کنید.");
                return;
            }

            // TODO: بعداً به ApiClient واقعی متصل می‌شود
            // فعلاً با داده‌های ساختگی تست می‌کنیم
            try {
                // شبیه‌سازی درخواست به سرور
                if ("admin@email.com".equals(email) && "admin123".equals(password)) {
                    // ورود به‌عنوان ادمین
                    SessionManager.setSession(
                            "mock-token-admin",
                            java.util.UUID.randomUUID(),
                            "مدیر سیستم",
                            "ADMIN"
                    );
                    AlertUtil.showSuccess("ورود موفق! خوش آمدید مدیر سیستم");
                    SceneManager.showDashboardPage();
                } else if ("user@email.com".equals(email) && "user123".equals(password)) {
                    // ورود به‌عنوان کاربر عادی
                    SessionManager.setSession(
                            "mock-token-user",
                            java.util.UUID.randomUUID(),
                            "کاربر عادی",
                            "USER"
                    );
                    AlertUtil.showSuccess("ورود موفق! خوش آمدید");
                    SceneManager.showDashboardPage();
                } else {
                    AlertUtil.showError("نام کاربری یا رمز عبور اشتباه است.");
                }
            } catch (Exception ex) {
                ExceptionHandler.handle(ex);
            }
        });

        // لینک ثبت‌نام
        Hyperlink registerLink = new Hyperlink("ثبت‌نام نکرده‌اید؟ کلیک کنید");
        registerLink.setStyle("-fx-font-size: 12px;");
        registerLink.setOnAction(e -> SceneManager.showRegisterPage());

        vbox.getChildren().addAll(title, emailField, passwordField, loginBtn, registerLink);
        return vbox;
    }
}