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
import model.Category;
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
import java.util.*;
import java.util.stream.Collectors;

public class NewAdController {

    @FXML private TextField titleField;
    @FXML private TextField addressField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField imagePathField;
    @FXML private VBox serviceFields;
    @FXML private VBox productFields;
    @FXML private ComboBox<String> serviceCalcTypeCombo;
    @FXML private TextField servicePriceField;
    @FXML private ComboBox<String> productConditionCombo;
    @FXML private ComboBox<String> categoryCombo;   // ← این کامبوباکس برای نمایش دسته‌بندی‌ها
    @FXML private TextField brandField;
    @FXML private TextField productPriceField;
    @FXML private TextField modelField;
    @FXML private TextField manufacturerField;
    @FXML private VBox optionsContainer;

    private List<OptionRequest> options;
    private AdvType selectedAdvType;
    private final CategoryService categoryService = new CategoryService();
    private List<Category> allCategories;          // تمام دسته‌بندی‌ها از سرور
    private Map<String, Long> categoryNameToIdMap; // نگاشت نام نمایشی به شناسه

    @FXML
    public void initialize() {
        options = new ArrayList<>();

        // ===== شهرها =====
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

        // ===== وضعیت محصول =====
        productConditionCombo.getItems().addAll(
                ProductState.NEW.getPersianName(),
                ProductState.LIKE_NEW.getPersianName(),
                ProductState.GOOD.getPersianName(),
                ProductState.FAIR.getPersianName(),
                ProductState.DAMAGED.getPersianName(),
                ProductState.REFURBISHED.getPersianName()
        );

        // ===== نوع محاسبه خدمت =====
        serviceCalcTypeCombo.getItems().addAll(
                ServiceType.HOURLY.getPersianName(),
                ServiceType.DAILY.getPersianName(),
                ServiceType.WEEKLY.getPersianName(),
                ServiceType.MONTHLY.getPersianName(),
                ServiceType.ANNUAL.getPersianName(),
                ServiceType.FIXED.getPersianName()
        );

        // ===== نوع آگهی =====
        typeCombo.getItems().addAll("خدمت", "کالا");
        typeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            onChooseType();
        });

        // ===== مخفی کردن فیلدهای تخصصی در ابتدا =====
        productFields.setVisible(false);
        productFields.setManaged(false);
        serviceFields.setVisible(false);
        serviceFields.setManaged(false);

        // ===== بارگذاری دسته‌بندی‌ها =====
        loadCategories();
    }

    // ================================
    //  بارگذاری دسته‌بندی‌ها
    // ================================
    private void loadCategories() {
        try {
            allCategories = categoryService.getAllCategories();
            Platform.runLater(() -> {
                // فیلتر بر اساس نوع آگهی انتخاب‌شده
                AdvType filterType = selectedAdvType;
                List<Category> filtered = allCategories.stream()
                        .filter(cat -> filterType == null || cat.getType() == filterType)
                        .collect(Collectors.toList());

                // ساخت لیست نام‌های نمایشی با تورفتگی
                categoryNameToIdMap = new LinkedHashMap<>();
                List<String> displayNames = buildCategoryDisplayList(filtered);

                categoryCombo.getItems().clear();
                categoryCombo.getItems().addAll(displayNames);
                categoryCombo.getSelectionModel().selectFirst();
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت دسته‌بندی‌ها: " + e.getMessage());
        }
    }

    /**
     * ساخت لیست نام‌های نمایشی با تورفتگی برای نمایش سلسله‌مراتبی
     */
    private List<String> buildCategoryDisplayList(List<Category> categories) {
        // پیدا کردن ریشه‌ها (دسته‌بندی‌های بدون والد)
        List<Category> roots = categories.stream()
                .filter(cat -> cat.getParent() == null)
                .collect(Collectors.toList());

        List<String> result = new ArrayList<>();
        for (Category root : roots) {
            traverseCategoryTree(root, 0, result, categories);
        }
        return result;
    }

    /**
     * پیمایش درخت دسته‌بندی به صورت Depth-First و افزودن نام با تورفتگی
     */
    private void traverseCategoryTree(Category category, int depth, List<String> result, List<Category> all) {
        // نام با تورفتگی (با فاصله یا خط تیره)
        String indent = "  ".repeat(depth);
        String displayName = indent + category.getName();
        result.add(displayName);
        categoryNameToIdMap.put(displayName, category.getId());

        // پیدا کردن زیردسته‌ها
        List<Category> children = all.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(category.getId()))
                .collect(Collectors.toList());

        for (Category child : children) {
            traverseCategoryTree(child, depth + 1, result, all);
        }
    }

    // ================================
    //  تغییر نوع آگهی
    // ================================
    @FXML
    public void onChooseType() {
        String selected = typeCombo.getValue();
        boolean isProduct = "کالا".equals(selected);
        boolean isService = "خدمت".equals(selected);

        productFields.setVisible(isProduct);
        productFields.setManaged(isProduct);
        serviceFields.setVisible(isService);
        serviceFields.setManaged(isService);

        if (isProduct) {
            selectedAdvType = AdvType.PRODUCT;
        } else if (isService) {
            selectedAdvType = AdvType.SERVICE;
        } else {
            selectedAdvType = null;
        }

        // بارگذاری مجدد دسته‌بندی‌ها با نوع جدید
        loadCategories();
    }

    // ================================
    //  انتخاب تصویر (موقت)
    // ================================
    @FXML
    public void onChooseImage() {
        System.out.println("انتخاب تصویر کلیک شد");
        AlertUtil.showWarning("این قابلیت در حال توسعه است");
    }

    // ================================
    //  افزودن ویژگی
    // ================================
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

    // ================================
    //  دریافت شناسه دسته‌بندی انتخاب‌شده
    // ================================
    private Long getSelectedCategoryId() {
        String selectedDisplay = categoryCombo.getSelectionModel().getSelectedItem();
        if (selectedDisplay == null) return null;
        return categoryNameToIdMap.get(selectedDisplay);
    }

    // ================================
    //  ثبت آگهی
    // ================================
    @FXML
    public void onSubmit() {
        try {
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

            getAllFeatures();
            City city = City.fromPersianName(cityCombo.getValue());
            Long categoryId = getSelectedCategoryId();

            AdvertisementDetailDto result;

            if (selectedAdvType == AdvType.PRODUCT) {
                ValidationUtil.isNotEmpty(
                        productPriceField.getText(),
                        productConditionCombo.getValue()
                );
                ProductCreateRequest request = createProductRequest(city, categoryId);
                result = AdvService.createProduct(request);
            } else {
                ValidationUtil.isNotEmpty(
                        servicePriceField.getText(),
                        serviceCalcTypeCombo.getValue()
                );
                ServiceCreateRequest request = createServiceRequest(city, categoryId);
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

    // ================================
    //  ساخت درخواست محصول
    // ================================
    private ProductCreateRequest createProductRequest(City city, Long categoryId) {
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
        request.setCategoryId(categoryId);  // ← استفاده از شناسه دسته‌بندی
        request.setOptions(options);
        request.setImages(new ArrayList<>());
        return request;
    }

    // ================================
    //  ساخت درخواست خدمت
    // ================================
    private ServiceCreateRequest createServiceRequest(City city, Long categoryId) {
        ServiceCreateRequest request = new ServiceCreateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCostOfPart(new BigDecimal(servicePriceField.getText().trim()));
        request.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));
        request.setCategoryId(categoryId);  // ← استفاده از شناسه دسته‌بندی
        request.setOptions(options);
        request.setImages(new ArrayList<>());
        return request;
    }

    // ================================
    //  دریافت ویژگی‌ها
    // ================================
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

    // ================================
    //  انصراف
    // ================================
    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.LIST_ADS, null);
    }
}