package component;

import javafx.fxml.FXML;
import service.AuthService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;

public class SidebarController {

    @FXML
    public void onDashboard() {
        System.out.println("Dashboard clicked!");
        SceneManager.showPage(Pages.DASHBOARD, null);
    }

    @FXML
    public void onNewAd() {
        System.out.println("New ad clicked!");
        SceneManager.showPage(Pages.NEW_AD, null);
    }

    @FXML
    public void onFavorites() {
        System.out.println("Favorites clicked!");
        SceneManager.showPage(Pages.FAVORITES, null);
    }

    @FXML
    public void onChat() {
        System.out.println("Chat clicked!");
        SceneManager.showPage(Pages.CHAT, null);
    }

    @FXML
    public void onProfile() {
        SceneManager.showPage(Pages.PROFILE, null);
    }

    /**
     * خروج از حساب کاربری از طریق سایدبار
     */
    @FXML
    public void onLogout() {
        boolean confirm = AlertUtil.showConfirmation(
                "خروج از حساب",
                "آیا از خروج از حساب کاربری خود اطمینان دارید؟"
        );

        if (!confirm) {
            return;
        }

        try {
            AuthService.logout();
            AlertUtil.showSuccess("شما با موفقیت خارج شدید.");
            SceneManager.showPage(Pages.LOGIN, null);
        } catch (Exception e) {
            AlertUtil.showError("خطا در خروج از حساب: " + e.getMessage());
        }
    }
}