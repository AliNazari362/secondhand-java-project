package component;

import javafx.fxml.FXML;
import utils.*;

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
        Utils.logout();
    }
}