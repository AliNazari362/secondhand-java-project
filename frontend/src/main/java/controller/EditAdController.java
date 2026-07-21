package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Category;
import model.enums.AdvType;
import model.enums.City;
import model.enums.ProductState;
import model.enums.ServiceType;
import model.response.AdvertisementDetailDto;
import service.AdvService;
import service.CategoryService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class EditAdController {

    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private ComboBox<String> serviceTypeCombo;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField manufacturerField;
    @FXML private TextField addressField;

    private UUID adId;
    private AdvertisementDetailDto currentAd;
    private final CategoryService categoryService = new CategoryService();
    private List<Category> allCategories;
    private LinkedHashMap<String, Long> categoryNameToIdMap;
    private Long currentCategoryId; // شناسه دسته‌بندی فعلی

    // ================================
    //  دریافت شناسه آگهی از صفحه قبل
    // ================================
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
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت اطلاعات آگهی: " + e.getMessage());
        }
    }

    private void initializeComboBoxes() {
        // شهرها
        cityCombo.getItems().clear();
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }

        // نوع آگهی
        typeCombo.getItems().clear();
        typeCombo.getItems().addAll("خدمت", "کالا");

        // وضعیت محصول
        conditionCombo.getItems().clear();
        for (ProductState state : ProductState.values()) {
            conditionCombo.getItems().add(state.getPersianName());
        }

        // نوع محاسبه خدمت
        serviceTypeCombo.getItems().clear();
        for (ServiceType type : ServiceType.values()) {
            serviceTypeCombo.getItems().add(type.getPersianName());
        }
    }

    // ================================
    //  بارگذاری دسته‌بندی‌ها
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

                // ===== انتخاب دسته‌بندی فعلی با تطابق نام =====
                String currentCategoryName = currentAd.getCategoryName();
                if (currentCategoryName != null && !currentCategoryName.isEmpty()) {
                    // پیدا کردن دسته‌بندی با این نام
                    for (Category cat : allCategories) {
                        if (currentCategoryName.equals(cat.getName())) {
                            currentCategoryId = cat.getId();
                            // پیدا کردن نام نمایشی متناظر
                            for (String display : displayNames) {
                                Long id = categoryNameToIdMap.get(display);
                                if (id != null && id.equals(currentCategoryId)) {
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

    // ================================
    //  پر کردن فرم با داده‌های موجود
    // ================================
    private void populateForm() {
        if (currentAd == null) return;

        titleField.setText(currentAd.getFullName());
        descArea.setText(currentAd.getDescription());
        addressField.setText(currentAd.getAddress());

        if (currentAd.getCity() != null) {
            cityCombo.getSelectionModel().select(currentAd.getCity().getPersianName());
        }

        if (currentAd.getAdvType() == AdvType.PRODUCT) {
            typeCombo.getSelectionModel().select("کالا");
            if (currentAd.getProductDetail() != null) {
                priceField.setText(currentAd.getProductDetail().getPrice().toString());
                conditionCombo.getSelectionModel().select(
                        currentAd.getProductDetail().getStateOfProduct().getPersianName()
                );
                brandField.setText(currentAd.getProductDetail().getBrand());
                modelField.setText(currentAd.getProductDetail().getModel());
                manufacturerField.setText(currentAd.getProductDetail().getConstructor());
            }
        } else if (currentAd.getAdvType() == AdvType.SERVICE) {
            typeCombo.getSelectionModel().select("خدمت");
            if (currentAd.getServiceDetail() != null) {
                priceField.setText(currentAd.getServiceDetail().getCostOfPart().toString());
                serviceTypeCombo.getSelectionModel().select(
                        currentAd.getServiceDetail().getTypeOfPart().getPersianName()
                );
            }
        }
    }

    // ================================
    //  دریافت شناسه دسته‌بندی انتخاب‌شده
    // ================================
    private Long getSelectedCategoryId() {
        String selected = categoryCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return categoryNameToIdMap.get(selected);
    }

    // ================================
    //  به‌روزرسانی آگهی
    // ================================
    @FXML
    public void onUpdate() {
        // TODO: پیاده‌سازی کامل ویرایش با استفاده از ProductUpdateRequest یا ServiceUpdateRequest
        // از getSelectedCategoryId() برای دریافت شناسه دسته‌بندی جدید استفاده کنید
        System.out.println("Update clicked! Category ID: " + getSelectedCategoryId());
        AlertUtil.showWarning("قابلیت ویرایش در حال توسعه است");
    }

    @FXML
    public void onCancel() {
        SceneManager.showPage(Pages.AD_DETAIL, null, adId);
    }

    @FXML
    public void onAddOption() {
        // TODO: مشابه NewAdController پیاده‌سازی شود
        AlertUtil.showWarning("افزودن ویژگی در حال توسعه است");
    }

    @FXML
    public void onChooseImage() {
        AlertUtil.showWarning("انتخاب تصویر در حال توسعه است");
    }
}