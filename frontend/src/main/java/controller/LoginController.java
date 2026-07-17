package controller;

import exception.ExceptionHandler;
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
        // کارت سفید
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("card");

        // لوگو
        Text logo = new Text("🛒");
        logo.setStyle("-fx-font-size: 48px;");

        // عنوان
        Text title = new Text("خوش آمدید");
        title.getStyleClass().add("title");

        // زیرعنوان
        Text subtitle = new Text("وارد حساب کاربری خود شوید");
        subtitle.getStyleClass().add("subtitle");

        // فیلد ایمیل
        TextField emailField = new TextField();
        emailField.setPromptText("آدرس ایمیل");
        emailField.getStyleClass().add("input-field");
        emailField.setMaxWidth(320);

        // فیلد رمز عبور
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("رمز عبور");
        passwordField.getStyleClass().add("input-field");
        passwordField.setMaxWidth(320);

        // دکمه ورود
        Button loginBtn = new Button("ورود به حساب");
        loginBtn.getStyleClass().add("primary-btn");
        loginBtn.setMaxWidth(320);
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                AlertUtil.showError("لطفاً همه فیلدها را پر کنید.");
                return;
            }

            try {
                // --- داده‌های ساختگی برای تست (بعداً با ApiClient جایگزین می‌شود) ---
                if ("admin@email.com".equals(email) && "admin123".equals(password)) {
                    SessionManager.setSession("mock-token-admin", java.util.UUID.randomUUID(), "مدیر سیستم", "ADMIN");
                    AlertUtil.showSuccess("ورود موفق! خوش آمدید مدیر سیستم");
                    SceneManager.showDashboardPage();
                } else if ("user@email.com".equals(email) && "user123".equals(password)) {
                    SessionManager.setSession("mock-token-user", java.util.UUID.randomUUID(), "کاربر عادی", "USER");
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
        Hyperlink registerLink = new Hyperlink("ثبت‌نام نکرده‌اید؟ همین حالا ثبت‌نام کنید");
        registerLink.getStyleClass().add("link");
        registerLink.setOnAction(e -> SceneManager.showRegisterPage());

        // پس‌زمینه اصلی
        VBox mainBox = new VBox(15);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(30));
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // اضافه کردن کارت به صفحه
        card.getChildren().addAll(logo, title, subtitle, emailField, passwordField, loginBtn, registerLink);
        mainBox.getChildren().add(card);

        // ✅ متصل کردن CSS (رفع خطای static)
        mainBox.getStylesheets().add(LoginController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}