package controller;

import exception.ExceptionHandler;
import javafx.scene.control.*;
import model.response.AdvertisementSummaryDto;
import model.response.UserSummaryDto;
import service.ApiClient;
import utils.AlertUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class AdminController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "پنل مدیریت");

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPrefWidth(800);

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");

        // تب ۱: آگهی‌های در انتظار
        Tab pendingTab = new Tab("📋 آگهی‌های در انتظار");
        VBox pendingBox = new VBox(10);
        pendingBox.setPadding(new Insets(15));

        ListView<AdvertisementSummaryDto> pendingListView = new ListView<>();
        pendingListView.getStyleClass().add("list-view");
        pendingListView.setPrefHeight(350);
        pendingListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AdvertisementSummaryDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellBox = new VBox(3);
                    Label titleLabel = new Label(item.getFullName());
                    Label infoLabel = new Label(item.getCity() + " - " + item.getOwnerFullName() + " - منتظر بررسی");
                    cellBox.getChildren().addAll(titleLabel, infoLabel);
                    setGraphic(cellBox);
                }
            }
        });


        HBox pendingBtnBox = new HBox(10);
        pendingBtnBox.setAlignment(Pos.CENTER);

        Button approveBtn = new Button("✅ تأیید");
        approveBtn.getStyleClass().add("success-btn");
        approveBtn.setOnAction(e -> {
            AdvertisementSummaryDto selected = pendingListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    ApiClient.put("/admin/approve-adv/" + selected.getId(), "");
                    AlertUtil.showSuccess("آگهی تأیید شد!");
                } catch (Exception ex) {
                    ExceptionHandler.handle(ex);
                }
            }
        });

        Button rejectBtn = new Button("❌ رد");
        rejectBtn.getStyleClass().add("danger-btn");
        rejectBtn.setOnAction(e -> {
            AdvertisementSummaryDto selected = pendingListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    // TODO add rejection explanation
                    ApiClient.put("/admin/reject-adv/" + selected.getId(), "دلیل رد");
                    AlertUtil.showSuccess("آگهی رد شد!");
                } catch (Exception ex) {
                    ExceptionHandler.handle(ex);
                }
            }
        });

        pendingBtnBox.getChildren().addAll(approveBtn, rejectBtn);

        pendingBox.getChildren().addAll(pendingListView, pendingBtnBox);
        pendingTab.setContent(pendingBox);

        // تب ۲: مدیریت کاربران
        Tab usersTab = new Tab("👥 کاربران");
        VBox usersBox = new VBox(10);
        usersBox.setPadding(new Insets(15));

        ListView<UserSummaryDto> usersListView = new ListView<>();
        usersListView.getStyleClass().add("list-view");
        usersListView.setPrefHeight(350);
        usersListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UserSummaryDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellBox = new VBox(3);
                    Label infoLabel = new Label(item.getFullName());
                    cellBox.getChildren().addAll(infoLabel);
                    setGraphic(cellBox);
                }
            }
        });

        HBox usersBtnBox = new HBox(10);
        usersBtnBox.setAlignment(Pos.CENTER);

        Button banBtn = new Button("⛔ بن");
        banBtn.getStyleClass().add("danger-btn");
        banBtn.setOnMouseClicked(e -> {
            UserSummaryDto selected = usersListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    ApiClient.put("/admin/ban-user/" + selected.getId(), null);
                    AlertUtil.showSuccess("یوزر بن شد!");
                } catch (Exception ex) {
                    ExceptionHandler.handle(ex);
                }
            }
        });

        Button unbanBtn = new Button("✅ آن‌بن");
        unbanBtn.getStyleClass().add("success-btn");
        unbanBtn.setOnMouseClicked(e -> {
            UserSummaryDto selected = usersListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    ApiClient.put("/admin/unban-user/" + selected.getId(), null);
                    AlertUtil.showSuccess("یوزر آن بن شد!");
                } catch (Exception ex) {
                    ExceptionHandler.handle(ex);
                }
            }
        });
        usersBtnBox.getChildren().addAll(banBtn, unbanBtn);

        usersBox.getChildren().addAll(usersListView, usersBtnBox);
        usersTab.setContent(usersBox);

        tabPane.getTabs().addAll(pendingTab, usersTab);

        card.getChildren().add(tabPane);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(AdminController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}