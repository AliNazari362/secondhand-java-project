package controller;

import model.request.LoginRequest;
import model.response.LoginResponse;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import service.AuthService;
import utils.SceneManager;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private VBox root;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // می‌توانیم آیکون‌ها را به فیلدها اضافه کنیم (اختیاری)
        // در اینجا فقط یک نمونه از تنظیمات اولیه
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Notifications.create()
                    .title("خطا")
                    .text("لطفاً همه فیلدها را پر کنید.")
                    .graphic(new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE))
                    .showError();
            return;
        }

        try {
            LoginRequest request = new LoginRequest(email, password);
            LoginResponse response = authService.login(request);

            // ذخیره در SessionManager
            SessionManager.setSession(
                    response.getToken(),
                    response.getUserId(),
                    response.getFullName(),
                    response.getRole().name()
            );

            // نمایش اعلان موفقیت با ControlsFX
            Notifications.create()
                    .title("ورود موفق")
                    .text("خوش آمدید " + response.getFullName())
                    .graphic(new FontIcon(FontAwesomeSolid.CHECK_CIRCLE))
                    .showInformation();

            // رفتن به صفحه داشبورد
//            SceneManager.showDashboardPage();  // بعداً پیاده‌سازی می‌شود

        } catch (Exception e) {
            Notifications.create()
                    .title("خطا در ورود")
                    .text(e.getMessage())
                    .graphic(new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE))
                    .showError();
        }
    }

    @FXML
    public void goToRegister() {
        // بعداً پیاده‌سازی می‌شود
//        SceneManager.showRegisterPage(); // فعلاً کامنت
    }
}