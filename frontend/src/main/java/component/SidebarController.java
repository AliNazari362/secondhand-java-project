package component;

import javafx.fxml.FXML;
import utils.Pages;
import utils.SceneManager;

public class SidebarController {

    @FXML public void onDashboard() {
        System.out.println("Dashboard clicked!");
    }

    @FXML public void onNewAd() {
        System.out.println("New ad clicked!");
    }

    @FXML public void onFavorites() {
        System.out.println("Favorites clicked!");
    }

    @FXML public void onChat() {
        System.out.println("Chat clicked!");
    }

    @FXML
    public void onProfile() {
        SceneManager.showPage(Pages.PROFILE, null);  // به جای System.out.println
    }

    @FXML public void onLogout() {
        System.out.println("Logout clicked!");
    }
}