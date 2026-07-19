package controller;

import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AdminController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");

        Text title = new Text("⚙️ پنل مدیریت");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Button backBtn = new Button("🔙 بازگشت");
        backBtn.getStyleClass().add("secondary-btn");
        backBtn.setOnAction(e -> SceneManager.showDashboardPage());

        HBox rightBox = new HBox(backBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, rightBox);

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

        ListView<String> pendingListView = new ListView<>();
        pendingListView.getStyleClass().add("list-view");
        pendingListView.setPrefHeight(350);
        pendingListView.getItems().clear();
        pendingListView.getItems().addAll(
                "لپ‌تاپ اچ‌پی - ۱۵,۰۰۰,۰۰۰ تومان - تهران - منتظر بررسی",
                "خدمات طراحی سایت - ۳۰۰,۰۰۰ تومان - اصفهان - منتظر بررسی"
        );

        HBox pendingBtnBox = new HBox(10);
        pendingBtnBox.setAlignment(Pos.CENTER);
        Button approveBtn = new Button("✅ تأیید");
        approveBtn.getStyleClass().add("success-btn");
        Button rejectBtn = new Button("❌ رد");
        rejectBtn.getStyleClass().add("danger-btn");
        pendingBtnBox.getChildren().addAll(approveBtn, rejectBtn);

        pendingBox.getChildren().addAll(pendingListView, pendingBtnBox);
        pendingTab.setContent(pendingBox);

        // تب ۲: مدیریت کاربران
        Tab usersTab = new Tab("👥 کاربران");
        VBox usersBox = new VBox(10);
        usersBox.setPadding(new Insets(15));

        ListView<String> usersListView = new ListView<>();
        usersListView.getStyleClass().add("list-view");
        usersListView.setPrefHeight(350);
        usersListView.getItems().clear();
        usersListView.getItems().addAll(
                "علی رضایی - کاربر عادی",
                "مدیر سیستم - ادمین",
                "احمد محمدی - کاربر عادی"
        );

        HBox usersBtnBox = new HBox(10);
        usersBtnBox.setAlignment(Pos.CENTER);
        Button banBtn = new Button("⛔ بن");
        banBtn.getStyleClass().add("danger-btn");
        Button unbanBtn = new Button("✅ آن‌بن");
        unbanBtn.getStyleClass().add("success-btn");
        usersBtnBox.getChildren().addAll(banBtn, unbanBtn);

        usersBox.getChildren().addAll(usersListView, usersBtnBox);
        usersTab.setContent(usersBox);

        tabPane.getTabs().addAll(pendingTab, usersTab);

        card.getChildren().add(tabPane);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(AdminController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}