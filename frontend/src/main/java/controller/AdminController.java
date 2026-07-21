package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import model.DashboardStats;
import service.AdminService;
import utils.AlertUtil;

public class AdminController {

    @FXML private TabPane tabPane;
    @FXML private ListView<String> pendingListView;
    @FXML private ListView<String> usersListView;

    // المان‌های داشبورد جدید
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label bannedUsersLabel;
    @FXML private Label deletedUsersLabel;
    @FXML private Label totalAdsLabel;
    @FXML private Label pendingAdsLabel;
    @FXML private Label activeAdsLabel;
    @FXML private Label soldAdsLabel;
    @FXML private Label rejectedAdsLabel;
    @FXML private Label totalMessagesLabel;
    @FXML private Label totalCommentsLabel;

    private final AdminService adminService = new AdminService();

    @FXML
    public void initialize() {
        // وقتی تب عوض می‌شود، اگر تب آمار انتخاب شد، آمار را بارگذاری کن
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab.getText().contains("آمار")) {
                loadDashboardStats();
            }
        });
    }

    @FXML
    private void loadDashboardStats() {
        try {
            DashboardStats stats = adminService.getDashboardStats();
            Platform.runLater(() -> {
                totalUsersLabel.setText(String.valueOf(stats.getTotalUsers()));
                activeUsersLabel.setText(String.valueOf(stats.getActiveUsers()));
                bannedUsersLabel.setText(String.valueOf(stats.getBannedUsers()));
                deletedUsersLabel.setText(String.valueOf(stats.getDeletedUsers()));
                totalAdsLabel.setText(String.valueOf(stats.getTotalAds()));
                pendingAdsLabel.setText(String.valueOf(stats.getPendingAds()));
                activeAdsLabel.setText(String.valueOf(stats.getActiveAds()));
                soldAdsLabel.setText(String.valueOf(stats.getSoldAds()));
                rejectedAdsLabel.setText(String.valueOf(stats.getRejectedAds()));
                totalMessagesLabel.setText(String.valueOf(stats.getTotalMessages()));
                totalCommentsLabel.setText(String.valueOf(stats.getTotalComments()));
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت آمار: " + e.getMessage());
        }
    }

    // متدهای قبلی (تایید، رد، بن، آن‌بن) با System.out.println همان‌طور که هستند
    @FXML public void onApproveAd() { System.out.println("Approve clicked!"); }
    @FXML public void onRejectAd() { System.out.println("Reject clicked!"); }
    @FXML public void onBanUser() { System.out.println("Ban clicked!"); }
    @FXML public void onUnbanUser() { System.out.println("Unban clicked!"); }
    @FXML public void goBack() { System.out.println("Go back clicked!"); }
}