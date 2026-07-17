package controller;

import utils.AlertUtil;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class NewAdController {
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

        Text title = new Text("📝 ثبت آگهی جدید");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Button backBtn = new Button("🔙 بازگشت");
        backBtn.getStyleClass().add("secondary-btn");
        backBtn.setOnAction(e -> SceneManager.showDashboardPage());

        HBox rightBox = new HBox(backBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, rightBox);

        // ---------- فرم ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        Text subtitle = new Text("اطلاعات آگهی");
        subtitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        TextField titleField = new TextField();
        titleField.setPromptText("عنوان آگهی *");
        titleField.getStyleClass().add("input-field");

        TextArea descArea = new TextArea();
        descArea.setPromptText("توضیحات کامل *");
        descArea.getStyleClass().add("text-area-field");
        descArea.setPrefHeight(120);
        descArea.setWrapText(true);

        TextField priceField = new TextField();
        priceField.setPromptText("قیمت (تومان) *");
        priceField.getStyleClass().add("input-field");

        ComboBox<String> cityCombo = new ComboBox<>();
        cityCombo.setPromptText("شهر *");
        cityCombo.getStyleClass().add("input-field");
        cityCombo.setMaxWidth(Double.MAX_VALUE);
        cityCombo.getItems().addAll("تهران", "اصفهان", "شیراز", "مشهد", "تبریز", "اهواز", "کرمان", "رشت", "یزد", "قم", "کرج");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setPromptText("نوع آگهی *");
        typeCombo.getStyleClass().add("input-field");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        typeCombo.getItems().addAll("محصول", "خدمات");

        // دکمه‌ها
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button submitBtn = new Button("📤 ثبت آگهی");
        submitBtn.getStyleClass().add("success-btn");

        Button cancelBtn = new Button("❌ انصراف");
        cancelBtn.getStyleClass().add("secondary-btn");
        cancelBtn.setOnAction(e -> SceneManager.showDashboardPage());

        submitBtn.setOnAction(e -> {
            // TODO: بعداً به ApiClient متصل می‌شود
            AlertUtil.showSuccess("آگهی با موفقیت ثبت شد!");
            SceneManager.showDashboardPage();
        });

        btnBox.getChildren().addAll(submitBtn, cancelBtn);

        card.getChildren().addAll(subtitle, titleField, descArea, priceField, cityCombo, typeCombo, btnBox);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);

        mainBox.getStylesheets().add(NewAdController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}