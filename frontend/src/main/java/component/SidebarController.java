package component;

import javafx.fxml.FXML;
import service.AuthService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.SessionManager;

public class SidebarController {

    @FXML
    public void onDashboard() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }

    @FXML
    public void onNewAd() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.NEW_AD, null);
    }

    @FXML
    public void onFavorites() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.FAVORITES, null);
    }

    @FXML
    public void onChat() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.CHAT, null);
    }

    @FXML
    public void onProfile() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.PROFILE, null);
    }

    @FXML
    public void onLogout() {
        boolean confirm = AlertUtil.showConfirmation("خروج از حساب", "آیا از خروج از حساب کاربری خود اطمینان دارید؟");
        if (!confirm) return;
        try {
            AuthService.logout();
            AlertUtil.showSuccess("شما با موفقیت خارج شدید.");
            SceneManager.showPage(Pages.LOGIN, null);
        } catch (Exception e) {
            AlertUtil.showError("خطا در خروج: " + e.getMessage());
        }
    }
}