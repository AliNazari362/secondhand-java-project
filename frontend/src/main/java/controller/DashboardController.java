package controller;

import service.SessionManager;
import utils.AlertUtil;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DashboardController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = createRoot();
        }
        return root;
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");

        Text title = new Text("📋 لیست آگهی‌ها");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Text userInfo = new Text("خوش آمدید، " + SessionManager.getFullName());
        userInfo.setStyle("-fx-font-size: 14px; -fx-fill: #718096;");

        Button logoutBtn = new Button("🚪 خروج");
        logoutBtn.getStyleClass().add("secondary-btn");
        logoutBtn.setOnAction(e -> {
            SessionManager.clear();
            SceneManager.showLoginPage();
        });

        Button newAdBtn = new Button("➕ آگهی جدید");
        newAdBtn.getStyleClass().add("primary-btn");
        newAdBtn.setOnAction(e -> SceneManager.showNewAdPage());

        HBox rightBox = new HBox(10, newAdBtn, logoutBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, userInfo, rightBox);

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        // کارت لیست
        VBox listCard = new VBox(10);
        listCard.getStyleClass().add("card");
        listCard.setPrefWidth(800);

        Label listTitle = new Label("آگهی‌های فعال");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        ListView<String> adListView = new ListView<>();
        adListView.getStyleClass().add("list-view");
        adListView.setPrefHeight(400);
        adListView.getItems().addAll(
                "لپ‌تاپ لنوو ThinkPad - ۱۸,۰۰۰,۰۰۰ تومان - تهران",
                "خدمات برنامه‌نویسی وب - ۵۰۰,۰۰۰ تومان/ساعت - اصفهان",
                "مبل هفت‌نفره - ۱۲,۰۰۰,۰۰۰ تومان - شیراز",
                "آموزش زبان انگلیسی - ۲۰۰,۰۰۰ تومان/جلسه - مشهد"
        );
        adListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                AlertUtil.showWarning("صفحه جزئیات آگهی در حال توسعه است.");
            }
        });

        listCard.getChildren().addAll(listTitle, adListView);

        content.getChildren().add(listCard);

        // ---------- جمع‌آوری ----------
        mainBox.getChildren().addAll(header, content);

        mainBox.getStylesheets().add(DashboardController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}