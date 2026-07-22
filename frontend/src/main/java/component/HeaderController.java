package component;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import utils.*;

public class HeaderController {

    @FXML
    private Text userInfoText;
    @FXML
    private Button adminBtn;

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
        SceneManager.showPage(Pages.CHAT_LIST, null);
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
    public void onAdmin() {
        if (!SessionManager.isLoggedIn() || !SessionManager.isAdmin()) {
            AlertUtil.showError("شما دسترسی ادمین ندارید.");
            return;
        }
        SceneManager.showPage(Pages.ADMIN, null);
    }

    @FXML
    public void onLogout() {
        Utils.logout();
    }
}