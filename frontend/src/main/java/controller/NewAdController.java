package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.enums.*;
import model.request.OptionRequest;
import model.request.ProductCreateRequest;
import model.request.ServiceCreateRequest;
import model.response.AdvertisementDetailDto;
import service.AdvService;
import utils.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NewAdController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField addressField;
    @FXML
    private TextArea descArea;
    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField imagePathField;
    @FXML
    private VBox serviceFields;
    @FXML
    private VBox productFields;

    // Service
    @FXML
    private ComboBox<String> serviceCalcTypeCombo;
    @FXML
    private TextField servicePriceField;

    // Product
    @FXML
    private ComboBox<String> productConditionCombo;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private TextField brandField;
    @FXML
    private TextField productPriceField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField manufacturerField;

    // Option
    @FXML
    private VBox optionsContainer;


    private List<OptionRequest> options;
    private AdvType selectedAdvType;

    @FXML
    public void initialize() {
        options = new ArrayList<>();

        cityCombo.getItems().addAll(
                City.TEHRAN.getPersianName(),
                City.ISFAHAN.getPersianName(),
                City.SHIRAZ.getPersianName(),
                City.MASHHAD.getPersianName(),
                City.TABRIZ.getPersianName(),
                City.AHVAZ.getPersianName(),
                City.KERMAN.getPersianName(),
                City.RASHT.getPersianName(),
                City.YAZD.getPersianName(),
                City.QOM.getPersianName(),
                City.KARAJ.getPersianName(),
                City.OTHER.getPersianName()
        );
        categoryCombo.getItems().addAll(
                Category.ELECTRONICS.getPersianName(),
                Category.VEHICLE.getPersianName(),
                Category.FURNITURE.getPersianName(),
                Category.BOOKS.getPersianName(),
                Category.CLOTHING.getPersianName(),
                Category.REAL_ESTATE.getPersianName(),
                Category.SPORTS.getPersianName(),
                Category.TOYS.getPersianName(),
                Category.OTHER.getPersianName()
        );
        productConditionCombo.getItems().addAll(
                ProductState.NEW.getPersianName(),
                ProductState.LIKE_NEW.getPersianName(),
                ProductState.GOOD.getPersianName(),
                ProductState.FAIR.getPersianName(),
                ProductState.DAMAGED.getPersianName(),
                ProductState.REFURBISHED.getPersianName()
        );
        serviceCalcTypeCombo.getItems().addAll(
                ServiceType.HOURLY.getPersianName(),
                ServiceType.DAILY.getPersianName(),
                ServiceType.WEEKLY.getPersianName(),
                ServiceType.MONTHLY.getPersianName(),
                ServiceType.ANNUAL.getPersianName(),
                ServiceType.FIXED.getPersianName()
        );
        typeCombo.getItems().addAll("خدمت", "کالا");

        productFields.setVisible(false);
        productFields.setManaged(false);
        serviceFields.setVisible(false);
        serviceFields.setManaged(false);
    }

    @FXML
    public void onChooseType() {
        String selected = typeCombo.getValue();

        boolean isProduct = "کالا".equals(selected);
        boolean isService = "خدمت".equals(selected);

        productFields.setVisible(isProduct);
        productFields.setManaged(isProduct);
        serviceFields.setVisible(isService);
        serviceFields.setManaged(isService);

        if (isProduct) selectedAdvType = AdvType.PRODUCT;
        else if (isService) selectedAdvType = AdvType.SERVICE;
    }


    @FXML
    public void onChooseImage() {
        // TODO this section must implement
        System.out.println("Choose image clicked!");
    }

    @FXML
    public void onSubmit() {
        ValidationUtil.isNotEmpty(
                titleField.getText(),
                addressField.getText(),
                descArea.getText(),
                cityCombo.getValue()
        );

        String advTypeStr = typeCombo.getValue();
        if (advTypeStr == null) {
            AlertUtil.showError("لطفاً نوع آگهی را انتخاب کنید");
            return;
        }
        try {
            getAllFeatures();
            System.out.println(SessionManager.getToken());
            City city = City.fromPersianName(cityCombo.getValue());
            AdvertisementDetailDto ads;
            if (selectedAdvType == AdvType.PRODUCT) {
                ValidationUtil.isNotEmpty(
                        productPriceField.getText(),
                        productConditionCombo.getValue(),
                        categoryCombo.getValue()
                );

                ProductCreateRequest newProduct = createProduct(city);
                ads = AdvService.createProduct(newProduct);
            } else {
                ValidationUtil.isNotEmpty(
                        servicePriceField.getText(),
                        serviceCalcTypeCombo.getValue()
                );

                ServiceCreateRequest newService = createService(city);
                ads = AdvService.createService(newService);
            }
            Platform.runLater(() -> AlertUtil.showSuccess(advTypeStr + " با موفقیت ثبت شد"));

            SceneManager.showPage(Pages.AD_DETAIL, ads.getFullName(), ads.getId());
        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (Exception e) {
            AlertUtil.showError(e.getMessage());
        }
    }

    private ServiceCreateRequest createService(City city) {
        ServiceCreateRequest newService = new ServiceCreateRequest();
        newService.setFullName(titleField.getText().trim());
        newService.setAddress(addressField.getText().trim());
        newService.setDescription(descArea.getText().trim());
        newService.setCity(city);
        newService.setCostOfPart(BigDecimal.valueOf(Long.parseLong(productPriceField.getText().trim())));
        newService.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));
        newService.setOptions(options);
        return newService;
    }

    private ProductCreateRequest createProduct(City city) {
        ProductCreateRequest newProduct = new ProductCreateRequest();
        newProduct.setFullName(titleField.getText().trim());
        newProduct.setAddress(addressField.getText().trim());
        newProduct.setDescription(descArea.getText().trim());
        newProduct.setCity(city);
        newProduct.setPrice(BigDecimal.valueOf(Long.parseLong(productPriceField.getText().trim())));
        newProduct.setStateOfProduct(ProductState.fromPersianName(productConditionCombo.getValue()));
        newProduct.setCategory(Category.fromPersianName(categoryCombo.getValue()));
        newProduct.setBrand(brandField.getText().trim());
        newProduct.setModel(modelField.getText().trim());
        newProduct.setConstructor(manufacturerField.getText().trim());
        newProduct.setOptions(options);
        return newProduct;
    }

    @FXML
    public void handleAddFeature() {
        HBox featureBox = new HBox(10);
        featureBox.setAlignment(Pos.CENTER_LEFT);
        featureBox.setPadding(new Insets(5));
        featureBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 5; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");

        TextField keyField = new TextField();
        keyField.setPromptText("کلید (مثال: رنگ)");
        keyField.setPrefWidth(150);

        TextField valueField = new TextField();
        valueField.setPromptText("مقدار (مثال: قرمز)");
        valueField.setPrefWidth(200);

        Button deleteBtn = new Button();
        deleteBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-cursor: hand;");
        deleteBtn.setText("✕");
        deleteBtn.setOnAction(e -> optionsContainer.getChildren().remove(featureBox));

        featureBox.getChildren().addAll(keyField, valueField, deleteBtn);
        optionsContainer.getChildren().add(featureBox);
    }

    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.LIST_ADS, null);
    }

    private void getAllFeatures() {
        options.clear();

        for (Node node : optionsContainer.getChildren()) {
            if (node instanceof HBox box) {
                TextField keyField = (TextField) box.getChildren().get(0);
                TextField valueField = (TextField) box.getChildren().get(1);

                String key = keyField.getText().trim();
                String value = valueField.getText().trim();

                if (!key.isEmpty() && !value.isEmpty()) {
                    options.add(new OptionRequest(key, value));
                }
            }
        }
    }
}