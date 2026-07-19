package controller;

import exception.ExceptionHandler;
import model.enums.*;
import model.request.ProductCreateRequest;
import model.request.ServiceCreateRequest;
import service.ApiClient;
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

import java.math.BigDecimal;
import java.util.Objects;

public class NewAdController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "📝 ثبت آگهی جدید");

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

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button submitBtn = new Button("📤 ثبت آگهی");
        submitBtn.getStyleClass().add("success-btn");
        submitBtn.setOnAction(e -> {
            if (titleField.getText().trim().isEmpty() ||
                    descArea.getText().trim().isEmpty() ||
                    priceField.getText().trim().isEmpty() ||
                    cityCombo.getValue() == null ||
                    typeCombo.getValue() == null) {
                AlertUtil.showError("لطفاً همه فیلدهای ضروری را پر کنید.");
                return;
            }
            try {
                BigDecimal price = new BigDecimal(priceField.getText());
                // TODO enums must be labels.
                City city = City.valueOf(cityCombo.getValue());
                AdvType advType = AdvType.valueOf(typeCombo.getValue());

                if (advType == AdvType.PRODUCT) {
                    ProductCreateRequest request = new ProductCreateRequest();
                    request.setFullName(titleField.getText());
                    request.setDescription(descArea.getText());
                    request.setCity(city);
                    request.setPrice(price);
                    // TODO category and state must be set
                    request.setCategory(Category.ELECTRONICS);
                    request.setStateOfProduct(ProductState.NEW);

                    ApiClient.post("/advs/create-product", request);
                } else {
                    ServiceCreateRequest request = new ServiceCreateRequest();
                    request.setFullName(titleField.getText());
                    request.setDescription(descArea.getText());
                    request.setCity(city);
                    request.setCostOfPart(price);
                    // TODO category and state must be set
                    request.setTypeOfPart(ServiceType.FIXED);
                    request.setSpecialCategory("عمومی");

                    ApiClient.post("/advs/create-service", request);
                }
                AlertUtil.showSuccess("آگهی با موفقیت ثبت شد!");
                SceneManager.showDashboardPage();
            } catch (Exception ex) {
                ExceptionHandler.handle(ex);
            }
        });


        Button cancelBtn = PublicElements.createCancelBtn();

        btnBox.getChildren().addAll(submitBtn, cancelBtn);

        card.getChildren().addAll(subtitle, titleField, descArea, priceField, cityCombo, typeCombo, btnBox);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(NewAdController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}