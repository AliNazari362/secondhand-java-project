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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
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
import utils.ImageUploadUtil;
import utils.Pages;
import utils.SceneManager;
import utils.ValidationUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for creating a new advertisement.
 * Supports 3-level hierarchical category selection:
 * Level 1: Root categories (filtered by ad type: PRODUCT or SERVICE)
 * Level 2: Sub-categories
 * Level 3: Leaf categories (final selection)
 */
public class NewAdController {

    // ===== FXML Fields =====
    @FXML private TextField titleField;
    @FXML private TextField addressField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private VBox serviceFields;
    @FXML private VBox productFields;
    @FXML private ComboBox<String> serviceCalcTypeCombo;
    @FXML private TextField servicePriceField;
    @FXML private ComboBox<String> productConditionCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField brandField;          // برند حفظ شده
    @FXML private TextField productPriceField;
    @FXML private TextField modelField;
    @FXML private TextField manufacturerField;
    @FXML private VBox optionsContainer;
    @FXML private FlowPane imagePreviewContainer;

    // ===== Internal State =====
    private List<OptionRequest> options;
    private AdvType selectedAdvType;
    private final CategoryService categoryService = new CategoryService();
    private List<Category> allCategories;
    private Map<String, Long> categoryNameToIdMap;

    // ===== For hierarchical category navigation =====
    private List<Category> rootCategories = new ArrayList<>();
    private Category currentSelectedCategory;
    private List<Category> currentLevelCategories = new ArrayList<>();
    private boolean isUpdating = false;

    // ===== Image Management =====
    private static final int MAX_IMAGES = 5;
    private static final int MAX_IMAGE_SIZE_MB = 5;
    private final List<File> selectedImageFiles = new ArrayList<>();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        options = new ArrayList<>();

        // ===== City ComboBox =====
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }

        // ===== Product Condition =====
        for (ProductState state : ProductState.values()) {
            productConditionCombo.getItems().add(state.getPersianName());
        }

        // ===== Service Calculation Type =====
        for (ServiceType type : ServiceType.values()) {
            serviceCalcTypeCombo.getItems().add(type.getPersianName());
        }

        // ===== Ad Type =====
        typeCombo.getItems().addAll("خدمت", "کالا");
        typeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            onChooseType();
        });

        // ===== Hide specific fields initially =====
        productFields.setVisible(false);
        productFields.setManaged(false);
        serviceFields.setVisible(false);
        serviceFields.setManaged(false);

        // ===== Load categories =====
        loadCategories();

        // ===== Setup category selection listener =====
        setupCategoryComboListener();
    }

    // ================================
    //  Category Management (3-Level Hierarchical)
    // ================================

    /**
     * Loads all categories from the backend and rebuilds parent-child relationships.
     */
    private void loadCategories() {
        try {
            allCategories = categoryService.getAllCategories();
            System.out.println("🔍 تعداد کل دسته‌بندی‌ها: " + allCategories.size());

            // بازسازی روابط با استفاده از parentId
            Map<Long, Category> categoryMap = new HashMap<>();
            for (Category cat : allCategories) {
                if (cat.getId() != null) {
                    categoryMap.put(cat.getId(), cat);
                }
            }

            for (Category cat : allCategories) {
                Long pid = cat.getParentId();
                if (pid != null) {
                    Category parent = categoryMap.get(pid);
                    if (parent != null) {
                        cat.setParent(parent);
                        parent.getSubCategories().add(cat);
                        System.out.println("🔗 " + cat.getName() + " ← " + parent.getName());
                    }
                }
            }

            Platform.runLater(() -> {
                isUpdating = true;
                populateCategoryComboBox();
                isUpdating = false;
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت دسته‌بندی‌ها: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Populates category combo box with root categories filtered by ad type.
     * Level 1: Root categories (e.g., "الکترونیک", "آشپزخانه")
     */
    private void populateCategoryComboBox() {
        AdvType filterType = selectedAdvType;
        System.out.println("🔍 فیلتر نوع: " + filterType);

        // فقط ریشه‌هایی که با نوع آگهی مطابقت دارند
        List<Category> roots = allCategories.stream()
                .filter(cat -> (filterType == null || cat.getType() == filterType) && cat.isRoot())
                .sorted(Comparator.comparing(Category::getName))
                .collect(Collectors.toList());

        rootCategories = roots;
        currentLevelCategories = roots;
        currentSelectedCategory = null;

        categoryNameToIdMap = new LinkedHashMap<>();
        List<String> displayNames = new ArrayList<>();

        for (Category root : roots) {
            displayNames.add(root.getName());
            categoryNameToIdMap.put(root.getName(), root.getId());
        }

        isUpdating = true;
        categoryCombo.getItems().clear();

        if (!roots.isEmpty()) {
            categoryCombo.getItems().addAll(displayNames);
            categoryCombo.getSelectionModel().selectFirst();
            categoryCombo.setDisable(false);
        } else {
            categoryCombo.getItems().add("هیچ دسته‌بندی موجود نیست");
            categoryCombo.setDisable(true);
        }
        isUpdating = false;
    }

    /**
     * Loads sub-categories (Level 2 or Level 3) into the combo box.
     * Adds a "Back" option to navigate to the previous level.
     */
    private void loadSubCategories(Category parent) {
        if (parent == null) return;

        List<Category> children = parent.getSubCategories();
        currentLevelCategories = children;
        currentSelectedCategory = parent;

        categoryNameToIdMap = new LinkedHashMap<>();
        List<String> displayNames = new ArrayList<>();

        // اگر زیردسته‌ای نباشد، این دسته‌بندی برگ (Leaf) است
        if (children == null || children.isEmpty()) {
            isUpdating = true;
            categoryCombo.getItems().clear();
            categoryCombo.getItems().add(parent.getName() + " ✓");
            categoryCombo.setDisable(true);
            isUpdating = false;
            return;
        }

        children.sort(Comparator.comparing(Category::getName));

        isUpdating = true;
        categoryCombo.getItems().clear();

        // گزینه بازگشت به سطح بالاتر
        categoryCombo.getItems().add("← بازگشت");
        categoryNameToIdMap.put("← بازگشت", -1L);

        for (Category child : children) {
            categoryCombo.getItems().add(child.getName());
            categoryNameToIdMap.put(child.getName(), child.getId());
        }

        categoryCombo.setDisable(false);
        categoryCombo.getSelectionModel().selectFirst();
        isUpdating = false;
    }

    /**
     * Listens to category combo box selection changes.
     */
    private void setupCategoryComboListener() {
        categoryCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdating) return;
            if (newVal == null) return;

            // گزینه بازگشت
            if ("← بازگشت".equals(newVal)) {
                goBackToParentLevel();
                return;
            }

            Long categoryId = categoryNameToIdMap.get(newVal);
            if (categoryId == null) return;

            Category selectedCat = findCategoryById(categoryId);
            if (selectedCat == null) return;

            // اگر زیردسته داشت، آن‌ها را بارگذاری کن (سطح بعدی)
            if (selectedCat.getSubCategories() != null && !selectedCat.getSubCategories().isEmpty()) {
                loadSubCategories(selectedCat);
            } else {
                // این دسته‌بندی برگ است (سطح سوم)
                currentSelectedCategory = selectedCat;
                System.out.println("✅ دسته‌بندی نهایی انتخاب شد: " + selectedCat.getName() + " (ID: " + selectedCat.getId() + ")");
            }
        });
    }

    /**
     * Goes back to the parent level in the category hierarchy.
     */
    private void goBackToParentLevel() {
        if (isUpdating) return;

        if (currentSelectedCategory == null) {
            return; // در سطح ریشه هستیم
        }

        Category parent = currentSelectedCategory.getParent();
        if (parent == null) {
            // بازگشت به ریشه‌ها
            populateCategoryComboBox();
        } else {
            // بازگشت به والد
            loadSubCategories(parent);
        }
    }

    /**
     * Finds a category by ID.
     */
    private Category findCategoryById(Long id) {
        for (Category cat : allCategories) {
            if (cat.getId().equals(id)) {
                return cat;
            }
        }
        return null;
    }

    /**
     * Returns the ID of the selected leaf category.
     */
    private Long getSelectedCategoryId() {
        if (currentSelectedCategory != null) {
            return currentSelectedCategory.getId();
        }
        return null;
    }

    // ================================
    //  Ad Type Change Handler
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

        if (allCategories != null) {
            isUpdating = true;
            populateCategoryComboBox();
            isUpdating = false;
        }
    }

    // ================================
    //  Image Upload
    // ================================

    @FXML
    public void onChooseImages() {
        int currentCount = selectedImageFiles.size();
        if (currentCount >= MAX_IMAGES) {
            AlertUtil.showWarning("حداکثر " + MAX_IMAGES + " تصویر می‌توانید انتخاب کنید.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصاویر آگهی");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "تصاویر (JPG, PNG, GIF, BMP, WEBP)",
                "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp", "*.webp"
        ));

        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files == null || files.isEmpty()) return;

        int remainingSlots = MAX_IMAGES - currentCount;
        if (files.size() > remainingSlots) {
            AlertUtil.showWarning("حداکثر می‌توانید " + remainingSlots + " تصویر دیگر انتخاب کنید.");
            files = files.subList(0, remainingSlots);
        }

        for (File file : files) {
            try {
                if (!ImageUploadUtil.isValidImageFile(file.getName())) {
                    AlertUtil.showWarning("فرمت فایل '" + file.getName() + "' پشتیبانی نمی‌شود.");
                    continue;
                }
                byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
                if (!ImageUploadUtil.isImageSizeValid(bytes, MAX_IMAGE_SIZE_MB)) {
                    AlertUtil.showWarning("حجم فایل '" + file.getName() + "' بیش از " + MAX_IMAGE_SIZE_MB + " مگابایت است.");
                    continue;
                }
                selectedImageFiles.add(file);
                addImagePreview(file);
            } catch (Exception e) {
                AlertUtil.showError("خطا در خواندن فایل: " + e.getMessage());
            }
        }
    }

    private void addImagePreview(File file) {
        try {
            Image image = new Image(file.toURI().toString(), 100, 100, true, true);

            VBox previewBox = new VBox(5);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 5;");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);

            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6; -fx-cursor: hand; -fx-background-radius: 50%;");
            removeBtn.setOnAction(e -> {
                selectedImageFiles.remove(file);
                imagePreviewContainer.getChildren().remove(previewBox);
            });

            long fileSize = file.length();
            String sizeStr = (fileSize / 1024) + " KB";
            Text sizeText = new Text(sizeStr);
            sizeText.setStyle("-fx-font-size: 9px; -fx-fill: #718096;");

            previewBox.getChildren().addAll(imageView, sizeText, removeBtn);
            imagePreviewContainer.getChildren().add(previewBox);

        } catch (Exception e) {
            AlertUtil.showError("خطا در بارگذاری پیش‌نمایش تصویر: " + e.getMessage());
        }
    }

    private List<ImageRequest> uploadAllImages() throws Exception {
        List<ImageRequest> results = new ArrayList<>();
        for (File file : selectedImageFiles) {
            try {
                String serverPath = ImageUploadUtil.uploadImageFromFile(file.toPath());
                results.add(new ImageRequest(serverPath));
            } catch (Exception e) {
                throw new Exception("خطا در آپلود تصویر '" + file.getName() + "': " + e.getMessage());
            }
        }
        return results;
    }

    // ================================
    //  Add Feature (Option)
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
    //  Submit Ad
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

            Long categoryId = getSelectedCategoryId();
            if (categoryId == null) {
                AlertUtil.showError("لطفاً یک دسته‌بندی را انتخاب کنید.");
                return;
            }

            getAllFeatures();

            List<ImageRequest> imageRequests = new ArrayList<>();
            if (!selectedImageFiles.isEmpty()) {
                imageRequests = uploadAllImages();
            }

            City city = City.fromPersianName(cityCombo.getValue());

            if (selectedAdvType == AdvType.PRODUCT) {
                ValidationUtil.isNotEmpty(
                        productPriceField.getText(),
                        productConditionCombo.getValue()
                );
                ProductCreateRequest request = createProductRequest(city, categoryId, imageRequests);
                AdvService.createProduct(request);
            } else {
                ValidationUtil.isNotEmpty(
                        servicePriceField.getText(),
                        serviceCalcTypeCombo.getValue()
                );
                ServiceCreateRequest request = createServiceRequest(city, categoryId, imageRequests);
                AdvService.createService(request);
            }

            Platform.runLater(() -> {
                AlertUtil.showSuccess(advTypeStr + " با موفقیت ثبت شد");
                SceneManager.showPage(Pages.LIST_ADS, null);
            });

        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (IllegalArgumentException e) {
            AlertUtil.showError(e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("خطا در ثبت آگهی: " + e.getMessage());
        }
    }

    private ProductCreateRequest createProductRequest(City city, Long categoryId, List<ImageRequest> images) {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setPrice(new BigDecimal(productPriceField.getText().trim()));
        request.setStateOfProduct(ProductState.fromPersianName(productConditionCombo.getValue()));
        request.setBrand(brandField.getText().trim());   // برند حفظ شد
        request.setModel(modelField.getText().trim());
        request.setConstructor(manufacturerField.getText().trim());
        request.setCategoryId(categoryId);
        request.setOptions(options);
        request.setImages(images);
        return request;
    }

    private ServiceCreateRequest createServiceRequest(City city, Long categoryId, List<ImageRequest> images) {
        ServiceCreateRequest request = new ServiceCreateRequest();
        request.setFullName(titleField.getText().trim());
        request.setDescription(descArea.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setCity(city);
        request.setCostOfPart(new BigDecimal(servicePriceField.getText().trim()));
        request.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));
        request.setCategoryId(categoryId);
        request.setOptions(options);
        request.setImages(images);
        return request;
    }

    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}