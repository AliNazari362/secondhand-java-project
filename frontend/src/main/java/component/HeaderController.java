package component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import utils.Pages;
import utils.SceneManager;

public class HeaderController {

    @FXML private Text titleText;
    @FXML private Text userInfoText;
    @FXML private Button adminBtn;

    // =============== متد initialize (اینجا قرار دهید) ===============
    @FXML
    public void initialize() {
        System.out.println("✅ هدر با موفقیت بارگذاری شد!");
    }
    // ================================================================

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
        System.out.println("Logout clicked!");
    }
}