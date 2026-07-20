package component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class HeaderController {

    @FXML private Text titleText;
    @FXML private Text userInfoText;
    @FXML private Button adminBtn;

    @FXML
    public void onNewAd() {
        System.out.println("New ad clicked!");
    }

    @FXML
    public void onFavorites() {
        System.out.println("Favorites clicked!");
    }

    @FXML
    public void onChat() {
        System.out.println("Chat clicked!");
    }

    @FXML
    public void onAdmin() {
        System.out.println("Admin clicked!");
    }

    @FXML
    public void onLogout() {
        System.out.println("Logout clicked!");
    }
}