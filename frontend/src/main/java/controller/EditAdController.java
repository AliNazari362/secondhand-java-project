package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import config.DataReceiver;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import model.enums.AdvType;
import model.enums.City;
import model.enums.ProductState;
import model.enums.ServiceType;
import model.request.OptionRequest;
import model.request.ProductUpdateRequest;
import model.request.ServiceUpdateRequest;
import model.response.AdvertisementDetailDto;
import service.AdvService;
import utils.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Controller for editing an existing advertisement.
 * <p>
 * Loads the current advertisement data, allows updating fields,
 * adding/removing images, and submitting changes to the backend.
 * Supports hierarchical category display with indentation.
 * </p>
 */
public class EditAdController implements DataReceiver {

    // ===== FXML Fields =====
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descArea;
    @FXML
    private TextField productPriceField;
    @FXML
    private TextField servicePriceField;
    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private ComboBox<String> productConditionCombo;
    @FXML
    private ComboBox<String> serviceCalcTypeCombo;
    @FXML
    private TextField brandField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField manufacturerField;
    @FXML
    private TextField addressField;
    @FXML
    private VBox optionsContainer;
    @FXML
    private FlowPane imagePreviewContainer;
    @FXML
    private VBox productFields;
    @FXML
    private VBox serviceFields;

    // ===== Internal State =====
    private UUID adId;
    private AdvertisementDetailDto currentAd;
    private final List<OptionRequest> options = new ArrayList<>();

    // ===== Image Management =====
    private final List<File> newImageFiles = new ArrayList<>();

    /**
     * Sets the advertisement ID and loads data.
     *
     * @param adId the UUID of the advertisement to edit
     */
    public void setAdId(UUID adId) {
        this.adId = adId;
        loadAdData();
    }

    private void loadAdData() {
        try {
            currentAd = AdvService.getAdvDetail(adId.toString());
            Platform.runLater(() -> {
                initializeComboBoxes();
                populateForm();
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت اطلاعات آگهی: " + e.getMessage());
        }
    }

    private void initializeComboBoxes() {
        cityCombo.getItems().clear();
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID) {
            setAdId((UUID) data);
        }
    }

    @FXML
    public void onChooseImages() {
        Utils.chooseImage(newImageFiles, imagePreviewContainer);
    }

    // ================================
    //  Add Feature (Option)
    // ================================

    @FXML
    public void onAddOption() {
        HBox featureBox = new HBox(10);
        featureBox.setAlignment(Pos.CENTER_LEFT);
        featureBox.setPadding(new Insets(5));
        featureBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 5; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");

        TextField keyField = new TextField();
        keyField.setPromptText("کلید");
        keyField.setPrefWidth(150);

        TextField valueField = new TextField();
        valueField.setPromptText("مقدار");
        valueField.setPrefWidth(200);

        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> optionsContainer.getChildren().remove(featureBox));

        featureBox.getChildren().addAll(keyField, valueField, deleteBtn);
        optionsContainer.getChildren().add(featureBox);
    }

    // ================================
    //  Populate Form
    // ================================

    private void populateForm() {
        if (currentAd == null) return;

        // ===== اطلاعات مشترک =====
        titleField.setText(currentAd.getFullName());
        descArea.setText(currentAd.getDescription());
        addressField.setText(currentAd.getAddress());

        if (currentAd.getCity() != null) {
            cityCombo.getSelectionModel().select(currentAd.getCity().getPersianName());
        }

        // ===== مدیریت نوع آگهی و نمایش بخش مربوطه =====
        if (currentAd.getAdvType() == AdvType.PRODUCT) {
            // نمایش بخش کالا، مخفی کردن بخش خدمت
            productFields.setVisible(true);
            productFields.setManaged(true);
            serviceFields.setVisible(false);
            serviceFields.setManaged(false);


            if (currentAd.getProductDetail() != null) {
                var detail = currentAd.getProductDetail();
                productPriceField.setText(detail.getPrice().toString());
                productConditionCombo.getSelectionModel().select(
                        detail.getStateOfProduct().getPersianName()
                );
                brandField.setText(detail.getBrand());
                modelField.setText(detail.getModel());
                manufacturerField.setText(detail.getConstructor());
            }
        } else if (currentAd.getAdvType() == AdvType.SERVICE) {
            // نمایش بخش خدمت، مخفی کردن بخش کالا
            serviceFields.setVisible(true);
            serviceFields.setManaged(true);
            productFields.setVisible(false);
            productFields.setManaged(false);

            if (currentAd.getServiceDetail() != null) {
                var detail = currentAd.getServiceDetail();
                servicePriceField.setText(detail.getCostOfPart().toString());
                serviceCalcTypeCombo.getSelectionModel().select(
                        detail.getTypeOfPart().getPersianName()
                );
            }
        }

        // ===== ویژگی‌های اضافی =====
        if (currentAd.getOptions() != null) {
            for (var opt : currentAd.getOptions()) {
                addExistingOption(opt.getOption(), opt.getValue());
            }
        }
    }

    private void addExistingOption(String key, String value) {
        HBox featureBox = new HBox(10);
        featureBox.setAlignment(Pos.CENTER_LEFT);
        featureBox.setPadding(new Insets(5));
        featureBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 5; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");

        TextField keyField = new TextField(key);
        keyField.setPrefWidth(150);

        TextField valueField = new TextField(value);
        valueField.setPrefWidth(200);

        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> optionsContainer.getChildren().remove(featureBox));

        featureBox.getChildren().addAll(keyField, valueField, deleteBtn);
        optionsContainer.getChildren().add(featureBox);
    }

    // ================================
    //  Update Ad
    // ================================

    @FXML
    public void onUpdate() {
        try {
            ValidationUtil.isNotEmpty(
                    titleField.getText(),
                    addressField.getText(),
                    descArea.getText(),
                    cityCombo.getValue()
            );

            Utils.getAllFeaturesOfAdv(options, optionsContainer);
            City city = City.fromPersianName(cityCombo.getValue());

            if (currentAd.getAdvType() == AdvType.PRODUCT) updateProduct(city);
            else if (currentAd.getAdvType() == AdvType.SERVICE) updateService(city);
            AlertUtil.showSuccess("آگهی با موفقیت به‌روزرسانی شد.");
            SceneManager.showPage(Pages.AD_DETAIL, null, adId);

        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void updateService(City city) throws Exception {
        if (servicePriceField.getText().trim().isEmpty()) {
            AlertUtil.showError("لطفاً هزینه خدمت را وارد کنید.");
            return;
        }
        if (serviceCalcTypeCombo.getValue() == null) {
            AlertUtil.showError("لطفاً نوع محاسبه خدمت را انتخاب کنید.");
            return;
        }

        // ساخت درخواست به‌روزرسانی خدمت
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCategoryId(null);
        request.setOptions(options);

        // فیلدهای اختصاصی خدمت
        request.setCostOfPart(new BigDecimal(servicePriceField.getText().trim()));
        request.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));

        // ارسال به سرور
        AdvService.updateService(adId.toString(), request);
    }

    private void updateProduct(City city) throws Exception {

        // اعتبارسنجی فیلدهای خدمت
        if (productPriceField.getText().trim().isEmpty()) {
            AlertUtil.showError("لطفاً قیمت کالا را وارد کنید.");
            return;
        }
        if (productConditionCombo.getValue() == null) {
            AlertUtil.showError("لطفاً وضعیت محصول را انتخاب کنید.");
            return;
        }

        // ساخت درخواست به‌روزرسانی کالا
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCategoryId(null);
        request.setOptions(options);

        // فیلدهای اختصاصی کالا
        request.setPrice(new BigDecimal(productPriceField.getText().trim()));
        request.setStateOfProduct(ProductState.fromPersianName(productConditionCombo.getValue()));
        request.setBrand(brandField.getText().trim());
        request.setModel(modelField.getText().trim());
        request.setConstructor(manufacturerField.getText().trim());

        // ارسال به سرور
        AdvService.updateProduct(adId.toString(), request);
    }

    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.AD_DETAIL, null, adId);
    }
}