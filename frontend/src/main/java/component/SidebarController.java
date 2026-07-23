package component;

import javafx.fxml.FXML;
import utils.*;

/**
 * Controller for the sidebar navigation component.
 * Handles navigation actions to main application pages with authentication checks.
 */
public class SidebarController {

    /**
     * Navigates to the main dashboard page.
     */
    @FXML
    public void onDashboard() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }

    /**
     * Navigates to the new advertisement creation page.
     * Redirects to login if the user is not authenticated.
     */
    @FXML
    public void onNewAd() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.NEW_AD, null);
    }

    /**
     * Navigates to the user's favorites page.
     * Redirects to login if the user is not authenticated.
     */
    @FXML
    public void onFavorites() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.FAVORITES, null);
    }

    /**
     * Navigates to the chat page.
     * Redirects to login if the user is not authenticated.
     */
    @FXML
    public void onChat() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.CHAT, null);
    }

    /**
     * Navigates to the user profile page.
     * Redirects to login if the user is not authenticated.
     */
    @FXML
    public void onProfile() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.PROFILE, null);
    }

    /**
     * Logs the current user out of the application.
     */
    @FXML
    public void onLogout() {
        Utils.logout();
    }
}