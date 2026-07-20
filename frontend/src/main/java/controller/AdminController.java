package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

public class AdminController {

    @FXML private TabPane tabPane;
    @FXML private ListView<String> pendingListView;
    @FXML private ListView<String> usersListView;

    @FXML
    public void onApproveAd() {
        System.out.println("Approve ad clicked!");
    }

    @FXML
    public void onRejectAd() {
        System.out.println("Reject ad clicked!");
    }

    @FXML
    public void onBanUser() {
        System.out.println("Ban user clicked!");
    }

    @FXML
    public void onUnbanUser() {
        System.out.println("Unban user clicked!");
    }

    @FXML
    public void goBack() {
        System.out.println("Go back clicked!");
    }
}