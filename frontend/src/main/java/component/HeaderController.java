package component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import service.AuthService;
import utils.AlertUtil;
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

        // نمایش دکمه ادمین فقط برای کاربران ادمین
        if (SessionManager.isLoggedIn() && SessionManager.isAdmin()) {
            adminBtn.setVisible(true);
            adminBtn.setManaged(true);
        } else {
            adminBtn.setVisible(false);
            adminBtn.setManaged(false);
        }

        // نمایش نام کاربر
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
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب کاربری خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
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

    /**
     * خروج از حساب کاربری
     * پس از تأیید کاربر، جلسه پاک شده و به صفحه ورود هدایت می‌شود.
     */
    @FXML
    public void onLogout() {
        // نمایش پیام تأیید
        boolean confirm = AlertUtil.showConfirmation(
                "خروج از حساب",
                "آیا از خروج از حساب کاربری خود اطمینان دارید؟"
        );

        if (!confirm) {
            return; // کاربر انصراف داد
        }

        try {
            // پاک کردن اطلاعات جلسه
            AuthService.logout();

            // نمایش پیام موفقیت
            AlertUtil.showSuccess("شما با موفقیت خارج شدید.");

            // هدایت به صفحه ورود
            SceneManager.showPage(Pages.LOGIN, null);

        } catch (Exception e) {
            AlertUtil.showError("خطا در خروج از حساب: " + e.getMessage());
        }
    }
}