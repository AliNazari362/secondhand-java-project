package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import model.DashboardStats;
import model.response.AdvertisementSummaryDto;
import model.response.UserSummaryDto;
import service.AdminService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the admin panel.
 * Handles user management (ban/unban), advertisement moderation (approve/reject),
 * system statistics (dashboard), and navigation to category management.
 */
public class AdminController {

    @FXML private TabPane tabPane;
    @FXML private ListView<String> pendingListView;
    @FXML private ListView<String> usersListView;
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

    // لیست‌های قابل مشاهده در UI
    private final ObservableList<String> userDisplayList = FXCollections.observableArrayList();
    private final ObservableList<String> pendingAdDisplayList = FXCollections.observableArrayList();

    // نقشه‌های نگاشت نام نمایشی به شناسه
    private final Map<String, String> userIdMap = new HashMap<>();
    private final Map<String, String> advIdMap = new HashMap<>();

    @FXML
    public void initialize() {
        // اتصال لیست‌ها به ListView
        usersListView.setItems(userDisplayList);
        pendingListView.setItems(pendingAdDisplayList);

        // بارگذاری اولیه داده‌ها
        loadUsers();
        loadPendingAds();

        // شنونده برای تغییر تب
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            String tabText = newTab.getText();
            if (tabText.contains("آمار")) {
                loadDashboardStats();
            } else if (tabText.contains("کاربران")) {
                loadUsers();
            } else if (tabText.contains("آگهی")) {
                loadPendingAds();
            }
        });
    }

    // ================================
    //  بارگذاری لیست کاربران
    // ================================
    private void loadUsers() {
        try {
            java.util.List<UserSummaryDto> users = adminService.getAllUsers();
            Platform.runLater(() -> {
                userDisplayList.clear();
                userIdMap.clear();
                for (UserSummaryDto user : users) {
                    String display = user.getFullName() + " (" + user.getEmail() + ")";
                    userDisplayList.add(display);
                    userIdMap.put(display, user.getId().toString());
                }
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در بارگذاری کاربران: " + e.getMessage());
        }
    }

    // ================================
    //  بارگذاری آگهی‌های در انتظار
    // ================================
    private void loadPendingAds() {
        try {
            java.util.List<AdvertisementSummaryDto> ads = adminService.getPendingAds();
            Platform.runLater(() -> {
                pendingAdDisplayList.clear();
                advIdMap.clear();
                for (AdvertisementSummaryDto ad : ads) {
                    String display = ad.getFullName() + " - " + ad.getCity().getPersianName();
                    pendingAdDisplayList.add(display);
                    advIdMap.put(display, ad.getId().toString());
                }
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در بارگذاری آگهی‌های در انتظار: " + e.getMessage());
        }
    }

    // ================================
    //  عملیات بن/آن‌بن کاربر
    // ================================
    @FXML
    public void onBanUser() {
        String selected = usersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("لطفاً یک کاربر را انتخاب کنید.");
            return;
        }
        String userId = userIdMap.get(selected);
        if (userId == null) {
            AlertUtil.showError("شناسه کاربر یافت نشد.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
                "بن کردن کاربر",
                "آیا از بن کردن کاربر '" + selected + "' اطمینان دارید؟"
        );
        if (!confirm) return;

        try {
            adminService.banUser(userId);
            AlertUtil.showSuccess("کاربر با موفقیت بن شد.");
            loadUsers(); // به‌روزرسانی لیست
        } catch (Exception e) {
            AlertUtil.showError("خطا در بن کردن کاربر: " + e.getMessage());
        }
    }

    @FXML
    public void onUnbanUser() {
        String selected = usersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("لطفاً یک کاربر را انتخاب کنید.");
            return;
        }
        String userId = userIdMap.get(selected);
        if (userId == null) {
            AlertUtil.showError("شناسه کاربر یافت نشد.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
                "رفع بن کاربر",
                "آیا از رفع بن کاربر '" + selected + "' اطمینان دارید؟"
        );
        if (!confirm) return;

        try {
            adminService.unbanUser(userId);
            AlertUtil.showSuccess("بن کاربر با موفقیت برداشته شد.");
            loadUsers();
        } catch (Exception e) {
            AlertUtil.showError("خطا در رفع بن کاربر: " + e.getMessage());
        }
    }

    // ================================
    //  عملیات تایید/رد آگهی
    // ================================
    @FXML
    public void onApproveAd() {
        String selected = pendingListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("لطفاً یک آگهی را انتخاب کنید.");
            return;
        }
        String advId = advIdMap.get(selected);
        if (advId == null) {
            AlertUtil.showError("شناسه آگهی یافت نشد.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
                "تایید آگهی",
                "آیا از تایید آگهی '" + selected + "' اطمینان دارید؟"
        );
        if (!confirm) return;

        try {
            adminService.approveAdv(advId);
            AlertUtil.showSuccess("آگهی با موفقیت تایید شد.");
            loadPendingAds();
        } catch (Exception e) {
            AlertUtil.showError("خطا در تایید آگهی: " + e.getMessage());
        }
    }

    @FXML
    public void onRejectAd() {
        String selected = pendingListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("لطفاً یک آگهی را انتخاب کنید.");
            return;
        }
        String advId = advIdMap.get(selected);
        if (advId == null) {
            AlertUtil.showError("شناسه آگهی یافت نشد.");
            return;
        }

        // دریافت دلیل رد از کاربر
        String reason = AlertUtil.showInputDialog(
                "رد آگهی",
                "لطفاً دلیل رد آگهی را وارد کنید:",
                ""
        );
        if (reason == null) {
            return; // کاربر انصراف داده
        }

        // اگر دلیل خالی بود، باز هم می‌توانیم ادامه دهیم یا پیام خطا بدهیم
        if (reason.trim().isEmpty()) {
            boolean confirm = AlertUtil.showConfirmation(
                    "رد بدون دلیل",
                    "آیا بدون وارد کردن دلیل، آگهی را رد می‌کنید؟"
            );
            if (!confirm) return;
        }

        try {
            // توجه: متد rejectAdv فعلاً فقط advId را می‌گیرد.
            // اگر بک‌اند نیاز به دلیل داشته باشد، باید متد را تغییر دهیم.
            // برای نمونه فعلی، دلیل را نادیده می‌گیریم.
            adminService.rejectAdv(advId);
            AlertUtil.showSuccess("آگهی با موفقیت رد شد.");
            loadPendingAds();
        } catch (Exception e) {
            AlertUtil.showError("خطا در رد آگهی: " + e.getMessage());
        }
    }

    // ================================
    //  بارگذاری آمار داشبورد
    // ================================
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

    // ================================
    //  ناوبری
    // ================================
    @FXML
    public void onManageCategories() {
        SceneManager.showPage(Pages.CATEGORY_MANAGEMENT, null);
    }

    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}