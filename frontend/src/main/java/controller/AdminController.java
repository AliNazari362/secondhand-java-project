package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.DashboardStats;
import model.response.AdvertisementDetailDto;
import model.response.AdvertisementSummaryDto;
import model.response.UserSummaryDto;
import service.AdminService;
import service.AdvService;
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

    @FXML
    private TabPane tabPane;
    @FXML
    private ListView<String> pendingListView;
    @FXML
    private ListView<String> usersListView;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label activeUsersLabel;
    @FXML
    private Label bannedUsersLabel;
    @FXML
    private Label deletedUsersLabel;
    @FXML
    private Label totalAdsLabel;
    @FXML
    private Label pendingAdsLabel;
    @FXML
    private Label activeAdsLabel;
    @FXML
    private Label soldAdsLabel;
    @FXML
    private Label rejectedAdsLabel;
    @FXML
    private Label totalMessagesLabel;
    @FXML
    private Label totalCommentsLabel;

    private final ObservableList<String> userDisplayList = FXCollections.observableArrayList();
    private final ObservableList<String> pendingAdDisplayList = FXCollections.observableArrayList();

    private final Map<String, String> userIdMap = new HashMap<>();
    private final Map<String, String> advIdMap = new HashMap<>();

    /**
     * Initializes the admin panel after FXML loading.
     * Sets up list views with data and adds tab change listeners
     * to reload data when switching between tabs.
     */
    @FXML
    public void initialize() {
        usersListView.setItems(userDisplayList);
        pendingListView.setItems(pendingAdDisplayList);

        loadUsers();
        loadPendingAds();

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

    /**
     * Loads all users from the server and populates the users list view.
     */
    private void loadUsers() {
        try {
            java.util.List<UserSummaryDto> users = AdminService.getAllUsers();
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
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Loads pending advertisements from the server and populates the pending ads list view.
     */
    private void loadPendingAds() {
        try {
            java.util.List<AdvertisementSummaryDto> ads = AdminService.getPendingAds();
            Platform.runLater(() -> {
                pendingAdDisplayList.clear();
                advIdMap.clear();

                for (AdvertisementSummaryDto ad : ads) {
                    String display = ad.getFullName() + " - " + ad.getCity().getPersianName();
                    pendingAdDisplayList.add(display);
                    advIdMap.put(display, ad.getId().toString());
                }

                setUIPendingList();
            });
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Configures the cell factory for the pending ads list view.
     * Adds a view button to each row for navigating to the advertisement detail.
     */
    private void setUIPendingList() {
        pendingListView.setCellFactory(param -> new ListCell<>() {
            private final Button viewButton = new Button("مشاهده");
            private final HBox container = new HBox(10, viewButton);

            {
                viewButton.setStyle("-fx-background-color: white;-fx-text-fill: #4299e1; -fx-border-color: #4299e1; -fx-font-size: 12px; -fx-padding: 4 12; -fx-background-radius: 4;");
                viewButton.setCursor(Cursor.HAND);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setText(item);
                    String advId = advIdMap.get(item);

                    viewButton.setOnAction(event -> {
                        try {
                            AdvertisementDetailDto ad = AdvService.getAdvDetail(advId);
                            SceneManager.showPage(Pages.AD_DETAIL, ad.getFullName(), ad.getId());
                        } catch (Exception e) {
                            ExceptionHandler.handle(e);
                        }
                    });

                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Retrieves the ID of the selected item from the list view and confirms the action with the user.
     *
     * @param forWhat   description of what is being acted upon (e.g., "کاربر", "آگهی")
     * @param action    description of the action being performed
     * @param map       the mapping from display text to entity ID
     * @param listView  the list view containing the selectable items
     * @return the selected entity ID, or null if no selection or confirmation was canceled
     */
    private String getIdForAction(String forWhat, String action, Map<String, String> map, ListView<String> listView) {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("لطفا یک " + forWhat + " انتخاب کنید");
            return null;
        }
        String id = map.get(selected);
        if (id == null) {
            AlertUtil.showError("شناسه " + forWhat + " یافت نشد");
            return null;
        }
        boolean confirm = AlertUtil.showConfirmation(
                action + " " + forWhat,
                "آیا از " + action + " " + forWhat + " اطمینان دارید؟"
        );
        if (!confirm) return null;
        return id;
    }

    /**
     * Bans the selected user after confirmation.
     * Reloads the users list on success.
     */
    @FXML
    public void onBanUser() {
        String userId = getIdForAction("کاربر", "بن کردن", userIdMap, usersListView);
        if (userId != null) {
            try {
                AdminService.banUser(userId);
                AlertUtil.showSuccess("کاربر با موفقیت بن شد.");
                loadUsers();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    /**
     * Unbans the selected user after confirmation.
     * Reloads the users list on success.
     */
    @FXML
    public void onUnbanUser() {
        String userId = getIdForAction("کاربر", "آن بن کردن", userIdMap, usersListView);
        if (userId != null) {
            try {
                AdminService.unbanUser(userId);
                AlertUtil.showSuccess("بن کاربر با موفقیت برداشته شد.");
                loadUsers();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    /**
     * Approves the selected pending advertisement after confirmation.
     * Reloads the pending ads list on success.
     */
    @FXML
    public void onApproveAd() {
        String advId = getIdForAction("آگهی", "تایید آگهی", advIdMap, pendingListView);
        if (advId != null) {
            try {
                AdminService.approveAdv(advId);
                AlertUtil.showSuccess("آگهی با موفقیت تایید شد.");
                loadPendingAds();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    /**
     * Rejects the selected pending advertisement after confirmation.
     * Prompts the user for an optional rejection reason.
     * Reloads the pending ads list on success.
     */
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

        String reason = AlertUtil.showInputDialog(
                "رد آگهی",
                "لطفاً دلیل رد آگهی را وارد کنید:",
                ""
        );
        if (reason == null) {
            return;
        }

        if (reason.trim().isEmpty()) {
            boolean confirm = AlertUtil.showConfirmation(
                    "رد بدون دلیل",
                    "آیا بدون وارد کردن دلیل، آگهی را رد می کنید؟"
            );
            if (!confirm) return;
        }

        try {
            AdminService.rejectAdv(advId);
            AlertUtil.showSuccess("آگهی با موفقیت رد شد.");
            loadPendingAds();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Loads dashboard statistics from the server and updates all stat labels.
     *
     * @return true if statistics were loaded successfully, false otherwise
     */
    private boolean loadDashboardStats() {
        try {
            DashboardStats stats = AdminService.getDashboardStats();
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
            return true;
        } catch (Exception e) {
            ExceptionHandler.handle(e);
            return false;
        }
    }

    /**
     * Handles the refresh statistics button click.
     * Reloads dashboard statistics and shows a success message on completion.
     */
    @FXML
    private void onStateClicked() {
        if (loadDashboardStats())
            AlertUtil.showSuccess("آمار با موفقیت بروز شد");
    }

    /**
     * Navigates to the category management page.
     */
    @FXML
    public void onManageCategories() {
        SceneManager.showPage(Pages.CATEGORY_MANAGEMENT, null);
    }

    /**
     * Navigates back to the main dashboard page.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}