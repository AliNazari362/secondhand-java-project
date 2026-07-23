package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Category;
import model.enums.*;
import model.request.ImageRequest;
import model.request.OptionRequest;
import model.request.ProductCreateRequest;
import model.request.ServiceCreateRequest;
import service.AdvService;
import service.CategoryService;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Controller for creating a new advertisement.
 * Supports hierarchical category selection using parentId reconstruction.
 */
public class NewAdController {

    // ===== FXML Fields =====
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
    @FXML
    private FlowPane imagePreviewContainer;

    // ===== Internal State =====
    private List<OptionRequest> options;
    private AdvType selectedAdvType;
    private List<Category> allCategories;
    private Map<String, Long> categoryNameToIdMap;

    // ===== For hierarchical category navigation =====
    private Category currentSelectedCategory;
    private boolean isUpdating = false;

    // ===== Image Management =====
    private final List<File> selectedImageFiles = new ArrayList<>();

    /**
     * Initializes the new advertisement form.
     * Populates combo boxes with enum values and sets up type-based field visibility.
     */
    @FXML
    public void initialize() {
        options = new ArrayList<>();

        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }

        for (ProductState state : ProductState.values()) {
            productConditionCombo.getItems().add(state.getPersianName());
        }

        for (ServiceType type : ServiceType.values()) {
            serviceCalcTypeCombo.getItems().add(type.getPersianName());
        }

        typeCombo.getItems().addAll("خدمت", "کالا");
        typeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onChooseType());

        productFields.setVisible(false);
        productFields.setManaged(false);
        serviceFields.setVisible(false);
        serviceFields.setManaged(false);

        loadCategories();
        setupCategoryComboListener();
    }

    /**
     * Loads all categories from the server and reconstructs parent-child relationships
     * using the parentId field. Populates the category combo box with root-level categories.
     */
    private void loadCategories() {
        try {
            allCategories = CategoryService.getAllCategories();

            Map<Long, Category> categoryMap = new HashMap<>();
            for (Category cat : allCategories) {
                if (cat.getId() != null) {
                    categoryMap.put(cat.getId(), cat);
                }
            }

            for (Category cat : allCategories) {
                cat.getSubCategories().clear();
            }

            Utils.loadAllCategories(allCategories, categoryMap);
            Platform.runLater(() -> {
                isUpdating = true;
                populateCategoryComboBox();
                isUpdating = false;
            });

        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Populates the category combo box with root-level categories filtered by the selected ad type.
     */
    private void populateCategoryComboBox() {
        AdvType filterType = selectedAdvType;

        List<Category> roots = allCategories.stream()
                .filter(cat -> (filterType == null || cat.getType() == filterType) && cat.isRoot())
                .sorted(Comparator.comparing(Category::getName))
                .toList();

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
            categoryCombo.getItems().add("هیچ دسته بندی موجود نیست");
            categoryCombo.setDisable(true);
        }
        isUpdating = false;
    }

    /**
     * Loads subcategories of the given parent category into the combo box.
     * If the parent has no children, shows it as a leaf selection with a back option.
     *
     * @param parent the parent category whose subcategories should be displayed
     */
    private void loadSubCategories(Category parent) {
        if (parent == null) {
            return;
        }

        List<Category> children = parent.getSubCategories();

        if (children == null || children.isEmpty()) {
            isUpdating = true;
            categoryCombo.getItems().clear();

            categoryCombo.getItems().add("← بازگشت");
            categoryNameToIdMap.put("← بازگشت", -1L);

            categoryCombo.getItems().add(parent.getName() + " ✓");
            categoryNameToIdMap.put(parent.getName() + " ✓", parent.getId());

            categoryCombo.setDisable(false);
            categoryCombo.getSelectionModel().selectLast();

            currentSelectedCategory = parent;

            isUpdating = false;
            return;
        }

        children.sort(Comparator.comparing(Category::getName));

        currentSelectedCategory = parent;

        categoryNameToIdMap = new LinkedHashMap<>();

        isUpdating = true;
        categoryCombo.getItems().clear();

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
     * Sets up the listener for category combo box selection changes.
     * Handles back navigation and drills down into subcategories.
     */
    private void setupCategoryComboListener() {
        categoryCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdating) {
                return;
            }
            if (newVal == null) {
                return;
            }

            if ("← بازگشت".equals(newVal)) {
                goBackToParentLevel();
                return;
            }

            Long categoryId = categoryNameToIdMap.get(newVal);
            if (categoryId == null) {
                return;
            }

            Category selectedCat = findCategoryById(categoryId);
            if (selectedCat == null) {
                return;
            }

            if (!selectedCat.getSubCategories().isEmpty()) {
                loadSubCategories(selectedCat);
            } else {
                currentSelectedCategory = selectedCat;
            }
        });
    }

    /**
     * Navigates back to the parent level in the category hierarchy.
     * If already at root level, reloads the root categories.
     */
    private void goBackToParentLevel() {
        if (isUpdating) return;

        if (currentSelectedCategory == null) {
            populateCategoryComboBox();
            return;
        }

        Category parent = currentSelectedCategory.getParent();
        if (parent == null) {
            populateCategoryComboBox();
        } else {
            loadSubCategories(parent);
        }
    }

    /**
     * Finds a category by its ID from the loaded categories list.
     *
     * @param id the category ID to search for
     * @return the matching Category, or null if not found
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
     * Returns the ID of the currently selected category in the combo box.
     *
     * @return the selected category ID, or null if no category is selected
     */
    private Long getSelectedCategoryId() {
        String selected = categoryCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return categoryNameToIdMap.get(selected);
    }

    /**
     * Handles the ad type selection change.
     * Toggles visibility between product and service fields and refreshes categories.
     */
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

    /**
     * Opens the image chooser dialog for selecting advertisement images.
     */
    @FXML
    public void onChooseImages() {
        Utils.chooseImage(selectedImageFiles, imagePreviewContainer);
    }

    /**
     * Uploads all selected image files to the server.
     *
     * @return a list of ImageRequest objects with server paths
     * @throws Exception if any image upload fails
     */
    private List<ImageRequest> uploadAllImages() throws Exception {
        List<ImageRequest> results = new ArrayList<>();
        for (File file : selectedImageFiles) {
            try {
                String serverPath = ImageUploadUtil.uploadImageFromFile(file.toPath());
                results.add(new ImageRequest(serverPath));
            } catch (Exception e) {
                throw new IOException("خطا در آپلود تصویر '" + file.getName() + "': " + e.getMessage());
            }
        }
        return results;
    }

    /**
     * Adds a new key-value feature input row to the options' container.
     */
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

    /**
     * Validates the form and submits the new advertisement to the server.
     * Routes to the appropriate create method based on the selected advertisement type.
     */
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
                AlertUtil.showError("لطفاً یک دسته بندی را انتخاب کنید.");
                return;
            }

            Utils.getAllFeaturesOfAdv(options, optionsContainer);

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

            AlertUtil.showSuccess(advTypeStr + " با موفقیت ثبت شد");
            SceneManager.showPage(Pages.DASHBOARD, null);

        } catch (NumberFormatException e) {
            AlertUtil.showError("قیمت باید عدد باشد");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Builds a ProductCreateRequest from the form fields.
     *
     * @param city       the selected city
     * @param categoryId the selected category ID
     * @param images     the list of uploaded image requests
     * @return a populated ProductCreateRequest
     */
    private ProductCreateRequest createProductRequest(City city, Long categoryId, List<ImageRequest> images) {
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
        request.setCategoryId(categoryId);
        request.setOptions(options);
        request.setImages(images);
        return request;
    }

    /**
     * Builds a ServiceCreateRequest from the form fields.
     *
     * @param city       the selected city
     * @param categoryId the selected category ID
     * @param images     the list of uploaded image requests
     * @return a populated ServiceCreateRequest
     */
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

    /**
     * Cancels ad creation and navigates back to the dashboard.
     */
    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}