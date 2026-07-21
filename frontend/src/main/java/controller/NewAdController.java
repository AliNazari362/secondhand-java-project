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
import model.request.ImageRequest;
import model.request.OptionRequest;
import model.request.ProductCreateRequest;
import model.request.ServiceCreateRequest;
import model.response.AdvertisementDetailDto;
import service.AdvService;
import service.CategoryService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.SessionManager;
import utils.ValidationUtil;

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
    @FXML
    private ComboBox<String> serviceCalcTypeCombo;
    @FXML
    private TextField servicePriceField;
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
    @FXML
    private VBox optionsContainer;

    private List<OptionRequest> options;
    private AdvType selectedAdvType;
    private final CategoryService categoryService = new CategoryService();
    private List<model.Category> categories;

    @FXML
    public void initialize() {
        options = new ArrayList<>();

        // مقداردهی کامبوباکس شهر
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

        // بارگذاری دسته‌بندی‌ها از بک‌اند
        loadCategories();

        // مقداردهی کامبوباکس وضعیت محصول
        productConditionCombo.getItems().addAll(
                ProductState.NEW.getPersianName(),
                ProductState.LIKE_NEW.getPersianName(),
                ProductState.GOOD.getPersianName(),
                ProductState.FAIR.getPersianName(),
                ProductState.DAMAGED.getPersianName(),
                ProductState.REFURBISHED.getPersianName()
        );

        // مقداردهی کامبوباکس نوع محاسبه خدمت
        serviceCalcTypeCombo.getItems().addAll(
                ServiceType.HOURLY.getPersianName(),
                ServiceType.DAILY.getPersianName(),
                ServiceType.WEEKLY.getPersianName(),
                ServiceType.MONTHLY.getPersianName(),
                ServiceType.ANNUAL.getPersianName(),
                ServiceType.FIXED.getPersianName()
        );

        // مقداردهی کامبوباکس نوع آگهی
        typeCombo.getItems().addAll("خدمت", "کالا");

        // مخفی کردن بخش‌های تخصصی تا زمانی که نوع آگهی انتخاب شود
        productFields.setVisible(false);
        productFields.setManaged(false);
        serviceFields.setVisible(false);
        serviceFields.setManaged(false);
    }

    private void loadCategories() {
        try {
            categories = categoryService.getAllCategories();
            categoryCombo.getItems().clear();
            for (model.Category cat : categories) {
                categoryCombo.getItems().add(cat.getName());
            }
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت دسته‌بندی‌ها: " + e.getMessage());
        }
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
        System.out.println("انتخاب تصویر کلیک شد");
        // TODO: پیاده‌سازی انتخاب فایل و آپلود
        AlertUtil.showWarning("این قابلیت در حال توسعه است");
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
    public void onSubmit() {
        try {
            // اعتبارسنجی فیلدهای ضروری
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

            // دریافت ویژگی‌های اضافی
            getAllFeatures();

            City city = City.fromPersianName(cityCombo.getValue());
            AdvertisementDetailDto result;

            if (selectedAdvType == AdvType.PRODUCT) {
                // اعتبارسنجی فیلدهای محصول
                ValidationUtil.isNotEmpty(
                        productPriceField.getText(),
                        productConditionCombo.getValue(),
                        categoryCombo.getValue()
                );

                ProductCreateRequest request = createProductRequest(city);
                result = AdvService.createProduct(request);

            } else {
                // اعتبارسنجی فیلدهای خدمت
                ValidationUtil.isNotEmpty(
                        servicePriceField.getText(),
                        serviceCalcTypeCombo.getValue()
                );

                ServiceCreateRequest request = createServiceRequest(city);
                result = AdvService.createService(request);
            }

            Platform.runLater(() -> AlertUtil.showSuccess(advTypeStr + " با موفقیت ثبت شد"));
            SceneManager.showPage(Pages.LIST_ADS, null);

        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (Exception e) {
            AlertUtil.showError("خطا در ثبت آگهی: " + e.getMessage());
        }
    }

    private ProductCreateRequest createProductRequest(City city) {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setPrice(new BigDecimal(productPriceField.getText().trim()));
        request.setStateOfProduct(ProductState.fromPersianName(productConditionCombo.getValue()));
        request.setBrand(brandField.getText().trim());
        request.setModel(modelField.getText().trim());
        request.setConstructor(manufacturerField.getText().trim());
        request.setOptions(options);

        // تنظیم categoryId بر اساس نام انتخاب‌شده
        String selectedCategory = categoryCombo.getValue();
        if (selectedCategory != null && categories != null) {
            for (model.Category cat : categories) {
                if (cat.getName().equals(selectedCategory)) {
                    request.setCategoryId(cat.getId());
                    break;
                }
            }
        }

        // تصاویر (فعلاً خالی)
        request.setImages(new ArrayList<>());

        return request;
    }

    private ServiceCreateRequest createServiceRequest(City city) {
        ServiceCreateRequest request = new ServiceCreateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCostOfPart(new BigDecimal(servicePriceField.getText().trim()));
        request.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));
        request.setOptions(options);

        // تنظیم categoryId بر اساس نام انتخاب‌شده
        String selectedCategory = categoryCombo.getValue();
        if (selectedCategory != null && categories != null) {
            for (model.Category cat : categories) {
                if (cat.getName().equals(selectedCategory)) {
                    request.setCategoryId(cat.getId());
                    break;
                }
            }
        }

        // تصاویر (فعلاً خالی)
        request.setImages(new ArrayList<>());

        return request;
    }

    private void getAllFeatures() {
        options.clear();
        for (Node node : optionsContainer.getChildren()) {
            if (node instanceof HBox box) {
                if (box.getChildren().size() >= 3) {
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

    // ==================== متدهایی که در FXML به آنها ارجاع داده شده ====================

    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.LIST_ADS, null);
    }
}