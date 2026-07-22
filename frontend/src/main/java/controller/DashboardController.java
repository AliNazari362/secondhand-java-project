package controller;

import component.AdCardController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Category;
import model.enums.AdvType;
import model.enums.City;
import model.response.AdvertisementSummaryDto;
import service.AdvService;
import service.CategoryService;
import utils.AlertUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the main dashboard page.
 * Handles search functionality with filters:
 * - Keyword search (title/description)
 * - Category filter (hierarchical categories)
 * - City filter
 * - Price range (min/max)
 * - Sorting
 */
public class DashboardController {

    // ===== FXML Fields =====
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private FlowPane adFlowPane;

    private Map<String, Long> categoryNameToIdMap;


    /**
     * Initializes the controller.
     * Sets up combo boxes, loads categories, and performs initial search.
     */
    @FXML
    public void initialize() {
        // ===== City ComboBox =====
        cityCombo.getItems().add("همه شهرها");
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }
        cityCombo.getSelectionModel().selectFirst();

        // ===== Sort ComboBox =====
        sortCombo.getItems().addAll(
                "جدید ترین",
                "قدیمی ترین",
                "ارزان ترین",
                "گران ترین",
                "بالاترین امتیاز"
        );
        sortCombo.getSelectionModel().selectFirst();

        // ===== Load categories =====
        loadCategories();

        // ===== Initial search =====
        performSearch();
    }

    /**
     * بارگذاری دسته‌بندی‌ها از سرور و پر کردن کامبوباکس با برگ‌ها (leaf nodes).
     * هر آیتم به صورت "نام دسته‌بندی (نوع)" نمایش داده می‌شود.
     */
    private void loadCategories() {
        try {

            List<Category> leafCategories = CategoryService.getAllCategories().stream()
                    .filter(cat -> cat.getSubCategories() == null || cat.getSubCategories().isEmpty())
                    .toList();

            categoryCombo.getItems().clear();
            categoryCombo.getItems().add("همه دسته بندی ها");
            categoryNameToIdMap = new LinkedHashMap<>();

            for (Category cat : leafCategories) {
                String displayName = cat.getName() + " (" + (cat.getType() == AdvType.PRODUCT ? "کالا" : "خدمت") + ")";
                categoryCombo.getItems().add(displayName);
                categoryNameToIdMap.put(displayName, cat.getId());
            }

            categoryCombo.getSelectionModel().selectFirst();

        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت دسته بندی ها: " + e.getMessage());
        }
    }

    /**
     * Handles the search button click.
     */
    @FXML
    public void onSearch() {
        performSearch();
    }

    /**
     * Performs the actual search with all filters.
     */
    private void performSearch() {
        try {
            // ===== Keyword =====
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) keyword = null;

            // ===== City =====
            String cityPersian = cityCombo.getSelectionModel().getSelectedItem();
            City cityEnum = null;
            if (cityPersian != null && !cityPersian.equals("همه شهرها")) {
                cityEnum = City.fromPersianName(cityPersian);
            }

            // ===== Category =====
            String categoryDisplay = categoryCombo.getSelectionModel().getSelectedItem();
            Long categoryId = null;
            if (categoryDisplay != null && !categoryDisplay.equals("همه دسته بندی ها")) {
                categoryId = categoryNameToIdMap.get(categoryDisplay);
            }

            // ===== Sort =====
            String sortBy = getSort();

            // ===== Price Range =====
            BigDecimal minPrice = null;
            BigDecimal maxPrice = null;
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = new BigDecimal(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = new BigDecimal(maxPriceField.getText().trim());
            }

            // ===== Execute Search =====

            List<AdvertisementSummaryDto> ads = AdvService.getActiveAds(
                    keyword,
                    cityEnum,
                    categoryId,
                    sortBy,
                    minPrice,
                    maxPrice
            );

            // ===== Display Results =====
            displayAds(ads);

        } catch (NumberFormatException e) {
            AlertUtil.showError("لطفا قیمت را به صورت عدد وارد کنید.");
        } catch (Exception e) {
            AlertUtil.showError("خطا در جستجو: " + e.getMessage());
        }
    }

    private String getSort() {
        String sortPersian = sortCombo.getSelectionModel().getSelectedItem();
        String sortBy = "newest";
        if (sortPersian != null) {
            switch (sortPersian) {
                case "جدید ترین" -> sortBy = "newest";
                case "قدیمی ترین" -> sortBy = "oldest";
                case "ارزان ترین" -> sortBy = "priceAsc";
                case "گران ترین" -> sortBy = "priceDesc";
                case "بالاترین امتیاز" -> sortBy = "ratingDesc";
            }
        }
        return sortBy;
    }

    /**
     * Displays the search results as ad cards in the FlowPane.
     */
    private void displayAds(List<AdvertisementSummaryDto> ads) {
        adFlowPane.getChildren().clear();

        if (ads == null || ads.isEmpty()) {
            VBox emptyMessage = new VBox();
            emptyMessage.setAlignment(Pos.CENTER);
            emptyMessage.setPadding(new Insets(50));
            Text message = new Text("هیچ آگهی با این فیلترها یافت نشد.");
            message.setStyle("-fx-font-size: 18px; -fx-fill: #a0aec0;");
            emptyMessage.getChildren().add(message);
            adFlowPane.getChildren().add(emptyMessage);
            adFlowPane.setAlignment(Pos.CENTER);
            return;
        }

        for (AdvertisementSummaryDto ad : ads) {
            VBox card = AdCardController.createAdCard(ad);
            adFlowPane.getChildren().add(card);
            adFlowPane.setAlignment(Pos.CENTER_LEFT);
        }
    }
}