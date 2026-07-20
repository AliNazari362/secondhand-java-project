//package component;
//
//import utils.SceneManager;
//import utils.SessionManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.text.Text;
//
//public class HeaderController {
//
//    @FXML private Text titleText;
//    @FXML private Text userInfoText;
//    @FXML private Button adminBtn;
//
//    @FXML
//    public void initialize() {
//        String fullName = SessionManager.getFullName();
//        userInfoText.setText("خوش آمدید، " + (fullName != null ? fullName : "کاربر"));
//        adminBtn.setVisible(SessionManager.isAdmin());
//    }
//
//    public void setTitle(String title) {
//        titleText.setText(title);
//    }
//
//    @FXML public void onNewAd() { SceneManager.showNewAdPage(); }
//    @FXML public void onFavorites() { SceneManager.showFavoritesPage(); }
//    @FXML public void onChat() { SceneManager.showChatPage(); }
//    @FXML public void onAdmin() { SceneManager.showAdminPage(); }
//    @FXML public void onLogout() {
//        SessionManager.clear();
//        SceneManager.showLoginPage();
//    }
//}