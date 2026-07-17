package controller;

import utils.AlertUtil;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.UUID;

public class AdDetailController {
    private static VBox root;

    public static VBox getRoot(UUID adId) {
        if (root == null) {
            root = createRoot(adId);
        }
        return root;
    }

    private static VBox createRoot(UUID adId) {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");

        Text title = new Text("📄 جزئیات آگهی");
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
        card.setPrefWidth(700);

        // عنوان
        Text adTitle = new Text("لپ‌تاپ لنوو ThinkPad");
        adTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #1a202c;");

        // وضعیت
        Label statusBadge = new Label("فعال");
        statusBadge.getStyleClass().addAll("status-badge", "status-active");

        // اطلاعات
        VBox infoBox = new VBox(8);
        Label priceLabel = new Label("💰 قیمت: ۱۸,۰۰۰,۰۰۰ تومان");
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Label cityLabel = new Label("📍 شهر: تهران");
        Label ownerLabel = new Label("👤 فروشنده: علی رضایی");
        Label dateLabel = new Label("📅 تاریخ ثبت: ۱۴۰۴/۰۱/۱۵");

        Label descTitle = new Label("توضیحات:");
        descTitle.setStyle("-fx-font-weight: bold; -fx-fill: #2d3748;");

        Text descText = new Text("لپ‌تاپ دست دوم در شرایط عالی. فقط یک سال استفاده شده. بدون خط و خش.");
        descText.setWrappingWidth(600);

        // دکمه‌ها
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button chatBtn = new Button("💬 پیام به فروشنده");
        chatBtn.getStyleClass().add("primary-btn");
        chatBtn.setOnAction(e -> AlertUtil.showWarning("صفحه چت در حال توسعه است."));

        Button favBtn = new Button("❤️ افزودن به علاقه‌مندی");
        favBtn.getStyleClass().add("secondary-btn");
        favBtn.setOnAction(e -> AlertUtil.showSuccess("به علاقه‌مندی‌ها اضافه شد."));

        Button rateBtn = new Button("⭐ امتیازدهی");
        rateBtn.getStyleClass().add("secondary-btn");
        rateBtn.setOnAction(e -> AlertUtil.showWarning("صفحه امتیازدهی در حال توسعه است."));

        btnBox.getChildren().addAll(chatBtn, favBtn, rateBtn);

        // جمع‌آوری
        infoBox.getChildren().addAll(priceLabel, cityLabel, ownerLabel, dateLabel);
        card.getChildren().addAll(adTitle, statusBadge, infoBox, descTitle, descText, btnBox);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);

        mainBox.getStylesheets().add(AdDetailController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}