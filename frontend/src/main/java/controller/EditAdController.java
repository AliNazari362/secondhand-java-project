package controller;

import exception.ExceptionHandler;
import model.enums.*;
import model.request.*;
import model.response.AdvertisementDetailDto;
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
import java.util.UUID;

public class EditAdController {

    public static VBox getRoot(UUID adId) {
        return createRoot(adId);
    }

    private static VBox createRoot(UUID adId) {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "✏️ ویرایش آگهی");

        // ---------- فرم ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        try {
            String response = ApiClient.get("/advs/" + adId);
            AdvertisementDetailDto adv = ApiClient.fromJson(response, AdvertisementDetailDto.class);

            Text subtitle = new Text("ویرایش اطلاعات آگهی");
            subtitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #2d3748;");

            TextField titleField = new TextField(adv.getFullName());
            titleField.getStyleClass().add("input-field");

            TextArea descArea = new TextArea(adv.getDescription());
            descArea.getStyleClass().add("text-area-field");
            descArea.setPrefHeight(120);
            descArea.setWrapText(true);

            // TODO ecpicail seciotns must created
//            TextField priceField = new TextField(adv.get);
//            priceField.getStyleClass().add("input-field");

            ComboBox<String> cityCombo = new ComboBox<>();
            cityCombo.setPromptText("شهر");
            cityCombo.getStyleClass().add("input-field");
            cityCombo.setMaxWidth(Double.MAX_VALUE);
            cityCombo.getItems().addAll("تهران", "اصفهان", "شیراز", "مشهد", "تبریز", "اهواز", "کرمان", "رشت", "یزد", "قم", "کرج");
            cityCombo.setValue("تهران");

            HBox btnBox = new HBox(10);
            btnBox.setAlignment(Pos.CENTER);

            Button updateBtn = new Button("💾 ذخیره تغییرات");
            updateBtn.getStyleClass().add("success-btn");
            updateBtn.setOnAction(e -> {
                try {
                    // TODO enums must be labels.
                    City city = City.valueOf(cityCombo.getValue());

                    if (adv.getAdvType() == AdvType.PRODUCT) {
                        ProductUpdateRequest request = new ProductUpdateRequest();
                        request.setFullName(titleField.getText());
                        request.setDescription(descArea.getText());
                        request.setCity(city);
                        // TODO category and state must be set
                        request.setCategory(Category.ELECTRONICS);
                        request.setStateOfProduct(ProductState.NEW);

                        ApiClient.post("/advs/create-product", request);
                    } else {
                        ServiceUpdateRequest request = new ServiceUpdateRequest();
                        request.setFullName(titleField.getText());
                        request.setDescription(descArea.getText());
                        request.setCity(city);
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
                AlertUtil.showSuccess("آگهی با موفقیت ویرایش شد!");
                SceneManager.showDashboardPage();
            });

            Button cancelBtn = PublicElements.createCancelBtn();

            btnBox.getChildren().addAll(updateBtn, cancelBtn);

            card.getChildren().addAll(subtitle, titleField, descArea, cityCombo, btnBox);
        } catch (Exception ex) {
            ExceptionHandler.handle(ex);
        }
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(EditAdController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}