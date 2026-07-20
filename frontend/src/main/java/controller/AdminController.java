//package controller;
//
//import model.response.AdvSummaryResponse;
//import model.response.UserSummaryResponse;
//import service.AdService;
//import service.UserService;
//import utils.AlertUtil;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TabPane;
//
//import java.util.List;
//import java.util.UUID;
//
//public class AdminController {
//
//    @FXML private TabPane tabPane;
//    @FXML private ListView<String> pendingListView;
//    @FXML private ListView<String> usersListView;
//
//    private final AdService adService = new AdService();
//    private final UserService userService = new UserService();
//
//    @FXML
//    public void initialize() {
//        loadPendingAds();
//        loadUsers();
//    }
//
//    private void loadPendingAds() {
//        try {
//            List<AdvSummaryResponse> pending = adService.getPendingAds();
//            pendingListView.getItems().clear();
//            for (AdvSummaryResponse ad : pending) {
//                pendingListView.getItems().add(
//                        ad.getFullName() + " - " + ad.getCity().name() + " - منتظر بررسی"
//                );
//            }
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری آگهی‌های در انتظار: " + e.getMessage());
//        }
//    }
//
//    private void loadUsers() {
//        try {
//            List<UserSummaryResponse> users = userService.getAllUsers();
//            usersListView.getItems().clear();
//            for (UserSummaryResponse user : users) {
//                usersListView.getItems().add(
//                        user.getFullName() + " - " + user.getUserType().name()
//                );
//            }
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری کاربران: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onApproveAd() {
//        // TODO: پیاده‌سازی تأیید
//        AlertUtil.showSuccess("آگهی با موفقیت تأیید شد.");
//        loadPendingAds();
//    }
//
//    @FXML
//    public void onRejectAd() {
//        // TODO: پیاده‌سازی رد
//        AlertUtil.showSuccess("آگهی رد شد.");
//        loadPendingAds();
//    }
//
//    @FXML
//    public void onBanUser() {
//        // TODO: پیاده‌سازی بن
//        AlertUtil.showSuccess("کاربر بن شد.");
//        loadUsers();
//    }
//
//    @FXML
//    public void onUnbanUser() {
//        // TODO: پیاده‌سازی آن‌بن
//        AlertUtil.showSuccess("کاربر آن‌بن شد.");
//        loadUsers();
//    }
//
//    @FXML
//    public void goBack() {
//        SceneManager.showDashboardPage();
//    }
//}