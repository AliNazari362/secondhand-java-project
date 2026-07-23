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

import java.math.BigDecimal;
import java.util.*;

/**
 * Controller for editing an existing advertisement.
 * <p>
 * Loads the current advertisement data, allows updating fields,
 * and submitting changes to the backend.
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
    private VBox productFields;
    @FXML
    private VBox serviceFields;

    // ===== Internal State =====
    private UUID adId;
    private AdvertisementDetailDto currentAd;
    private final List<OptionRequest> options = new ArrayList<>();

    /**
     * Sets the advertisement ID and triggers data loading from the server.
     *
     * @param adId the UUID of the advertisement to edit
     */
    public void setAdId(UUID adId) {
        this.adId = adId;
        loadAdData();
    }

    /**
     * Loads the full advertisement detail from the server and populates the form.
     */
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

    /**
     * Initializes the city combo box with all available cities.
     */
    private void initializeComboBoxes() {
        cityCombo.getItems().clear();
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }
    }

    /**
     * Receives the advertisement UUID from the previous page.
     *
     * @param data the advertisement UUID to edit
     */
    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID) {
            setAdId((UUID) data);
        }
    }

    /**
     * Adds a new key-value feature row to the options' container.
     */
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

    /**
     * Populates the edit form with the current advertisement's data.
     * Shows the appropriate type-specific fields (product or service)
     * and restores any existing custom options.
     */
    private void populateForm() {
        if (currentAd == null) return;

        titleField.setText(currentAd.getFullName());
        descArea.setText(currentAd.getDescription());
        addressField.setText(currentAd.getAddress());

        if (currentAd.getCity() != null) {
            cityCombo.getSelectionModel().select(currentAd.getCity().getPersianName());
        }

        if (currentAd.getAdvType() == AdvType.PRODUCT) {
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

        if (currentAd.getOptions() != null) {
            for (var opt : currentAd.getOptions()) {
                addExistingOption(opt.getOption(), opt.getValue());
            }
        }
    }

    /**
     * Adds an existing key-value option row to the options' container.
     *
     * @param key   the option key
     * @param value the option value
     */
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

    /**
     * Validates the form and submits the updated advertisement to the server.
     * Routes to the appropriate update method based on advertisement type.
     */
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
            AlertUtil.showSuccess("آگهی با موفقیت به روزرسانی شد.");
            SceneManager.showPage(Pages.AD_DETAIL, null, adId);

        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Builds and sends a service advertisement update request to the server.
     *
     * @param city the selected city for the advertisement
     * @throws Exception if validation fails or the server request errors
     */
    private void updateService(City city) throws Exception {
        if (servicePriceField.getText().trim().isEmpty()) {
            AlertUtil.showError("لطفاً هزینه خدمت را وارد کنید.");
            return;
        }
        if (serviceCalcTypeCombo.getValue() == null) {
            AlertUtil.showError("لطفاً نوع محاسبه خدمت را انتخاب کنید.");
            return;
        }

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCategoryId(null);
        request.setOptions(options);

        request.setCostOfPart(new BigDecimal(servicePriceField.getText().trim()));
        request.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));

        AdvService.updateService(adId.toString(), request);
    }

    /**
     * Builds and sends a product advertisement update request to the server.
     *
     * @param city the selected city for the advertisement
     * @throws Exception if validation fails or the server request errors
     */
    private void updateProduct(City city) throws Exception {

        if (productPriceField.getText().trim().isEmpty()) {
            AlertUtil.showError("لطفاً قیمت کالا را وارد کنید.");
            return;
        }
        if (productConditionCombo.getValue() == null) {
            AlertUtil.showError("لطفاً وضعیت محصول را انتخاب کنید.");
            return;
        }

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCategoryId(null);
        request.setOptions(options);

        request.setPrice(new BigDecimal(productPriceField.getText().trim()));
        request.setStateOfProduct(ProductState.fromPersianName(productConditionCombo.getValue()));
        request.setBrand(brandField.getText().trim());
        request.setModel(modelField.getText().trim());
        request.setConstructor(manufacturerField.getText().trim());

        AdvService.updateProduct(adId.toString(), request);
    }

    /**
     * Cancels editing and navigates back to the advertisement detail page.
     */
    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.AD_DETAIL, null, adId);
    }
}