package component;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import utils.*;

/**
 * Controller for the shared header component.
 * Manages navigation actions and user session display in the application header.
 */
public class HeaderController {

    @FXML
    private Text userInfoText;
    @FXML
    private Button adminBtn;

    /**
     * Initializes the header component after FXML loading.
     * Shows/hides the admin button based on user role and displays a welcome message for logged-in users.
     */
    @FXML
    public void initialize() {
        if (SessionManager.isLoggedIn() && SessionManager.isAdmin()) {
            adminBtn.setVisible(true);
            adminBtn.setManaged(true);
        } else {
            adminBtn.setVisible(false);
            adminBtn.setManaged(false);
        }

        if (SessionManager.isLoggedIn()) {
            String fullName = SessionManager.getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                userInfoText.setText(fullName + " عزیز خوش آمدید");
                userInfoText.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }
        }
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
     * Navigates to the chat list page.
     * Redirects to login if the user is not authenticated.
     */
    @FXML
    public void onChat() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }
        SceneManager.showPage(Pages.CHAT_LIST, null);
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
     * Navigates to the admin panel page.
     * Shows an error if the user is not logged in or does not have admin privileges.
     */
    @FXML
    public void onAdmin() {
        if (!SessionManager.isLoggedIn() || !SessionManager.isAdmin()) {
            AlertUtil.showError("شما دسترسی ادمین ندارید.");
            return;
        }
        SceneManager.showPage(Pages.ADMIN, null);
    }

    /**
     * Logs the current user out of the application.
     */
    @FXML
    public void onLogout() {
        Utils.logout();
    }
}