package component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import utils.Pages;
import utils.SceneManager;
import utils.SessionManager;

public class HeaderController {

    @FXML private Text titleText;
    @FXML private Text userInfoText;
    @FXML private Button adminBtn;

    @FXML
    public void initialize() {
        System.out.println("✅ هدر با موفقیت بارگذاری شد!");

        // تنظیم visibility دکمه‌ی ادمین بر اساس نقش کاربر
        // اگر کاربر لاگین کرده و نقش ADMIN دارد، دکمه را نشان بده
        if (SessionManager.isLoggedIn() && SessionManager.isAdmin()) {
            adminBtn.setVisible(true);
            adminBtn.setManaged(true);
        } else {
            adminBtn.setVisible(false);
            adminBtn.setManaged(false);
        }

        // همچنین می‌توانید نام کاربر را در هدر نمایش دهید
        if (SessionManager.isLoggedIn()) {
            String fullName = SessionManager.getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                userInfoText.setText("خوش آمدید، " + fullName);
            }
        }
    }

    @FXML
    public void onNewAd() {
        SceneManager.showPage(Pages.NEW_AD, null);
    }

    @FXML
    public void onFavorites() {
        SceneManager.showPage(Pages.FAVORITES, null);
    }

    @FXML
    public void onChat() {
        SceneManager.showPage(Pages.CHAT, null);
    }

    @FXML
    public void onProfile() {
        System.out.println("🔥 دکمه پروفایل کلیک شد!");
        SceneManager.showPage(Pages.PROFILE, null);
    }

    @FXML
    public void onAdmin() {
        SceneManager.showPage(Pages.ADMIN, null);
    }

    @FXML
    public void onLogout() {
        // TODO: پیاده‌سازی خروج (پاک کردن session و رفتن به صفحه لاگین)
        System.out.println("Logout clicked!");
    }
}