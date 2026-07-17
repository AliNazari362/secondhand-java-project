package controller;

import utils.AlertUtil;
import utils.SceneManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegisterController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("card");

        Text logo = new Text("📝");
        logo.setStyle("-fx-font-size: 48px;");

        Text title = new Text("ثبت‌نام");
        title.getStyleClass().add("title");

        Text subtitle = new Text("ایجاد حساب کاربری جدید");
        subtitle.getStyleClass().add("subtitle");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("نام کامل");
        fullNameField.getStyleClass().add("input-field");
        fullNameField.setMaxWidth(320);

        TextField emailField = new TextField();
        emailField.setPromptText("آدرس ایمیل");
        emailField.getStyleClass().add("input-field");
        emailField.setMaxWidth(320);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("رمز عبور (حداقل ۸ کاراکتر)");
        passwordField.getStyleClass().add("input-field");
        passwordField.setMaxWidth(320);

        TextField phoneField = new TextField();
        phoneField.setPromptText("شماره تماس (اختیاری)");
        phoneField.getStyleClass().add("input-field");
        phoneField.setMaxWidth(320);

        Button registerBtn = new Button("ثبت‌نام");
        registerBtn.getStyleClass().add("success-btn");
        registerBtn.setMaxWidth(320);
        registerBtn.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                AlertUtil.showError("لطفاً همه فیلدهای ضروری را پر کنید.");
                return;
            }
            if (password.length() < 8) {
                AlertUtil.showError("رمز عبور باید حداقل ۸ کاراکتر باشد.");
                return;
            }

            AlertUtil.showSuccess("ثبت‌نام موفق! حالا وارد شوید.");
            Platform.runLater(() -> SceneManager.showLoginPage());
        });

        Hyperlink loginLink = new Hyperlink("قبلاً ثبت‌نام کرده‌اید؟ وارد شوید");
        loginLink.getStyleClass().add("link");
        loginLink.setOnAction(e -> SceneManager.showLoginPage());

        VBox mainBox = new VBox(15);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(30));
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        card.getChildren().addAll(logo, title, subtitle, fullNameField, emailField, passwordField, phoneField, registerBtn, loginLink);
        mainBox.getChildren().add(card);

        mainBox.getStylesheets().add(RegisterController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}