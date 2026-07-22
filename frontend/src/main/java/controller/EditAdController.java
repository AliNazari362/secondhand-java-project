package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import config.DataReceiver;
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
import model.enums.AdvType;
import model.enums.City;
import model.enums.ProductState;
import model.enums.ServiceType;
import model.request.ImageRequest;
import model.request.OptionRequest;
import model.request.ProductUpdateRequest;
import model.request.ServiceUpdateRequest;
import model.response.AdvertisementDetailDto;
import model.response.ImageResponseDto;
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
 * Controller for editing an existing advertisement.
 * <p>
 * Loads the current advertisement data, allows updating fields,
 * adding/removing images, and submitting changes to the backend.
 * Supports hierarchical category display with indentation.
 * </p>
 */
public class EditAdController implements DataReceiver {

    // ===== FXML Fields =====
    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private TextField productPriceField;
    @FXML private TextField servicePriceField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> productConditionCombo;
    @FXML private ComboBox<String> serviceCalcTypeCombo;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField manufacturerField;
    @FXML private TextField addressField;
    @FXML private VBox optionsContainer;
    @FXML private FlowPane imagePreviewContainer;
    @FXML private VBox productFields;
    @FXML private VBox serviceFields;

    // ===== Internal State =====
    private UUID adId;
    private AdvertisementDetailDto currentAd;
    private final CategoryService categoryService = new CategoryService();
    private List<Category> allCategories;
    private LinkedHashMap<String, Long> categoryNameToIdMap;
    private List<OptionRequest> options = new ArrayList<>();

    // ===== Image Management =====
    private static final int MAX_IMAGES = 5;
    private static final int MAX_IMAGE_SIZE_MB = 5;
    private final List<File> newImageFiles = new ArrayList<>();
    private final List<String> existingImagePaths = new ArrayList<>();

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
                loadCategories();
                populateForm();
                loadExistingImages();
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

        typeCombo.getItems().clear();
        typeCombo.getItems().addAll("خدمت", "کالا");

        productConditionCombo.getItems().clear();
        for (ProductState state : ProductState.values()) {
            productConditionCombo.getItems().add(state.getPersianName());
        }

        serviceCalcTypeCombo.getItems().clear();
        for (ServiceType type : ServiceType.values()) {
            serviceCalcTypeCombo.getItems().add(type.getPersianName());
        }
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID) {
            setAdId((UUID) data);
        }
    }

    // ================================
    //  Category Management (سلسله‌مراتبی)
    // ================================

    private void loadCategories() {
        try {
            allCategories = categoryService.getAllCategories();
            Platform.runLater(() -> {
                AdvType filterType = currentAd.getAdvType();
                List<Category> filtered = allCategories.stream()
                        .filter(cat -> filterType == null || cat.getType() == filterType)
                        .collect(Collectors.toList());

                categoryNameToIdMap = new LinkedHashMap<>();
                List<String> displayNames = buildCategoryDisplayList(filtered);

                categoryCombo.getItems().clear();
                categoryCombo.getItems().addAll(displayNames);

                // انتخاب دسته‌بندی فعلی
                String currentCategoryName = currentAd.getCategoryName();
                if (currentCategoryName != null && !currentCategoryName.isEmpty()) {
                    for (Category cat : allCategories) {
                        if (currentCategoryName.equals(cat.getName())) {
                            Long catId = cat.getId();
                            for (String display : displayNames) {
                                Long id = categoryNameToIdMap.get(display);
                                if (id != null && id.equals(catId)) {
                                    categoryCombo.getSelectionModel().select(display);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت دسته‌بندی‌ها: " + e.getMessage());
        }
    }

    /**
     * Builds a hierarchical display list with indentation.
     */
    private List<String> buildCategoryDisplayList(List<Category> categories) {
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
     * Recursively traverses the category tree and builds display names with indentation.
     */
    private void traverseCategoryTree(Category category, int depth, List<String> result, List<Category> all) {
        String indent = "  ".repeat(depth);
        String displayName = indent + category.getName();
        result.add(displayName);
        categoryNameToIdMap.put(displayName, category.getId());

        List<Category> children = all.stream()
                .filter(c -> c.getParent() != null && c.getParent().getId().equals(category.getId()))
                .collect(Collectors.toList());

        for (Category child : children) {
            traverseCategoryTree(child, depth + 1, result, all);
        }
    }

    private Long getSelectedCategoryId() {
        String selected = categoryCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return categoryNameToIdMap.get(selected);
    }

    // ================================
    //  Load Existing Images
    // ================================

    private void loadExistingImages() {
        if (currentAd.getImages() != null) {
            existingImagePaths.clear();
            for (ImageResponseDto img : currentAd.getImages()) {
                existingImagePaths.add(img.getPath());
                addExistingImagePreview(img.getPath());
            }
        }
    }

    private void addExistingImagePreview(String imagePath) {
        try {
            File file = new File(imagePath);
            Image image;
            if (file.exists()) {
                image = new Image(file.toURI().toString(), 100, 100, true, true);
            } else {
                String serverUrl = "http://localhost:8080/" + imagePath;
                image = new Image(serverUrl, 100, 100, true, true);
            }

            VBox previewBox = new VBox(5);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #48bb78; -fx-border-radius: 8; -fx-padding: 5; -fx-border-width: 2;");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);

            Text label = new Text("موجود");
            label.setStyle("-fx-font-size: 9px; -fx-fill: #38a169;");

            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6; -fx-cursor: hand; -fx-background-radius: 50%;");
            removeBtn.setOnAction(e -> {
                existingImagePaths.remove(imagePath);
                imagePreviewContainer.getChildren().remove(previewBox);
            });

            previewBox.getChildren().addAll(imageView, label, removeBtn);
            imagePreviewContainer.getChildren().add(previewBox);

        } catch (Exception e) {
            // Ignore preview errors
        }
    }

    @FXML
    public void onChooseImages() {
        int currentCount = existingImagePaths.size() + newImageFiles.size();
        if (currentCount >= MAX_IMAGES) {
            AlertUtil.showWarning("حداکثر " + MAX_IMAGES + " تصویر می‌توانید انتخاب کنید.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصاویر جدید");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "تصاویر", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp", "*.webp"
        ));

        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files == null || files.isEmpty()) return;

        int remainingSlots = MAX_IMAGES - currentCount;
        if (files.size() > remainingSlots) {
            AlertUtil.showWarning("حداکثر می‌توانید " + remainingSlots + " تصویر دیگر اضافه کنید.");
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
                newImageFiles.add(file);
                addNewImagePreview(file);
            } catch (Exception e) {
                AlertUtil.showError("خطا در خواندن فایل: " + e.getMessage());
            }
        }
    }

    private void addNewImagePreview(File file) {
        try {
            Image image = new Image(file.toURI().toString(), 100, 100, true, true);

            VBox previewBox = new VBox(5);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #4299e1; -fx-border-radius: 8; -fx-padding: 5; -fx-border-width: 2;");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);

            Text label = new Text("جدید");
            label.setStyle("-fx-font-size: 9px; -fx-fill: #3182ce;");

            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6; -fx-cursor: hand; -fx-background-radius: 50%;");
            removeBtn.setOnAction(e -> {
                newImageFiles.remove(file);
                imagePreviewContainer.getChildren().remove(previewBox);
            });

            previewBox.getChildren().addAll(imageView, label, removeBtn);
            imagePreviewContainer.getChildren().add(previewBox);

        } catch (Exception e) {
            AlertUtil.showError("خطا در بارگذاری پیش‌نمایش: " + e.getMessage());
        }
    }

    private List<ImageRequest> uploadNewImages() throws Exception {
        List<ImageRequest> results = new ArrayList<>();
        for (File file : newImageFiles) {
            try {
                String serverPath = ImageUploadUtil.uploadImageFromFile(file.toPath());
                results.add(new ImageRequest(serverPath));
            } catch (Exception e) {
                throw new Exception("خطا در آپلود تصویر: " + e.getMessage());
            }
        }
        return results;
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

    private void getAllFeatures() {
        options.clear();
        for (Node node : optionsContainer.getChildren()) {
            if (node instanceof HBox box && box.getChildren().size() >= 3) {
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

            typeCombo.getSelectionModel().select("کالا");

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

            typeCombo.getSelectionModel().select("خدمت");

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
            // 1. اعتبارسنجی اولیه
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

            // 2. دریافت ویژگی‌های اضافی
            getAllFeatures();

            // 3. آپلود تصاویر جدید (در صورت وجود)
            List<ImageRequest> uploadedImages = new ArrayList<>();
            if (!newImageFiles.isEmpty()) {
                uploadedImages = uploadNewImages();
            }

            // 4. دریافت اطلاعات مشترک
            City city = City.fromPersianName(cityCombo.getValue());
            Long categoryId = getSelectedCategoryId();

            // 5. به‌روزرسانی بر اساس نوع آگهی
            if ("کالا".equals(advTypeStr)) {
                // اعتبارسنجی فیلدهای کالا
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
                request.setCategoryId(categoryId);
                request.setOptions(options);

                // فیلدهای اختصاصی کالا
                request.setPrice(new BigDecimal(productPriceField.getText().trim()));
                request.setStateOfProduct(ProductState.fromPersianName(productConditionCombo.getValue()));
                request.setBrand(brandField.getText().trim());
                request.setModel(modelField.getText().trim());
                request.setConstructor(manufacturerField.getText().trim());

                // ارسال به سرور
                AdvService.updateProduct(adId.toString(), request);

            } else if ("خدمت".equals(advTypeStr)) {
                // اعتبارسنجی فیلدهای خدمت
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
                request.setCategoryId(categoryId);
                request.setOptions(options);

                // فیلدهای اختصاصی خدمت
                request.setCostOfPart(new BigDecimal(servicePriceField.getText().trim()));
                request.setTypeOfPart(ServiceType.fromPersianName(serviceCalcTypeCombo.getValue()));

                // ارسال به سرور
                AdvService.updateService(adId.toString(), request);

            } else {
                AlertUtil.showError("نوع آگهی نامعتبر است.");
                return;
            }

            // 6. اگر به‌روزرسانی موفق بود
            AlertUtil.showSuccess("آگهی با موفقیت به‌روزرسانی شد.");
            SceneManager.showPage(Pages.AD_DETAIL, null, adId);

        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (Exception e) {
            AlertUtil.showError("خطا در به‌روزرسانی آگهی: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.AD_DETAIL, null, adId);
    }
}