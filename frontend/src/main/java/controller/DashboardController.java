package controller;

import component.AdCardController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Category;
import model.enums.City;
import model.response.AdvertisementSummaryDto;
import service.AdvService;
import service.CategoryService;
import utils.AlertUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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
    @FXML private TextField searchField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> sortCombo;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private FlowPane adFlowPane;

    // ===== Services =====
    private final CategoryService categoryService = new CategoryService();
    private List<Category> categories;

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
                "جدیدترین",
                "قدیمی‌ترین",
                "ارزان‌ترین",
                "گران‌ترین",
                "بالاترین امتیاز"
        );
        sortCombo.getSelectionModel().selectFirst();

        // ===== Load categories =====
        loadCategories();

        // ===== Initial search =====
        performSearch();
    }

    /**
     * Loads categories from the backend and populates the category combo box.
     */
    private void loadCategories() {
        try {
            categories = categoryService.getAllCategories();
            categoryCombo.getItems().clear();
            categoryCombo.getItems().add("همه دسته‌بندی‌ها");

            for (Category cat : categories) {
                categoryCombo.getItems().add(cat.getName());
            }
            categoryCombo.getSelectionModel().selectFirst();

        } catch (Exception e) {
            AlertUtil.showError("خطا در دریافت دسته‌بندی‌ها: " + e.getMessage());
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
            String categoryName = categoryCombo.getSelectionModel().getSelectedItem();
            Long categoryId = null;
            if (categoryName != null && !categoryName.equals("همه دسته‌بندی‌ها") && categories != null) {
                for (Category cat : categories) {
                    if (cat.getName().equals(categoryName)) {
                        categoryId = cat.getId();
                        break;
                    }
                }
            }

            // ===== Sort =====
            String sortPersian = sortCombo.getSelectionModel().getSelectedItem();
            String sortBy = "newest";
            if (sortPersian != null) {
                switch (sortPersian) {
                    case "جدیدترین" -> sortBy = "newest";
                    case "قدیمی‌ترین" -> sortBy = "oldest";
                    case "ارزان‌ترین" -> sortBy = "priceAsc";
                    case "گران‌ترین" -> sortBy = "priceDesc";
                    case "بالاترین امتیاز" -> sortBy = "ratingDesc";
                }
            }

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
            AlertUtil.showError("لطفاً قیمت را به صورت عدد وارد کنید.");
        } catch (Exception e) {
            AlertUtil.showError("خطا در جستجو: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays the search results as ad cards in the FlowPane.
     */
    private void displayAds(List<AdvertisementSummaryDto> ads) {
        adFlowPane.getChildren().clear();

        if (ads == null || ads.isEmpty()) {
            VBox emptyMessage = new VBox();
            emptyMessage.setAlignment(javafx.geometry.Pos.CENTER);
            emptyMessage.setPadding(new Insets(50));
            Text message = new Text("هیچ آگهی با این فیلترها یافت نشد.");
            message.setStyle("-fx-font-size: 18px; -fx-fill: #a0aec0;");
            emptyMessage.getChildren().add(message);
            adFlowPane.getChildren().add(emptyMessage);
            return;
        }

        for (AdvertisementSummaryDto ad : ads) {
            VBox card = createAdCard(ad);
            adFlowPane.getChildren().add(card);
        }
    }

    /**
     * Creates an ad card component for a single advertisement.
     */
    private VBox createAdCard(AdvertisementSummaryDto ad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ad-card.fxml"));
            VBox card = loader.load();

            AdCardController controller = loader.getController();
            controller.setData(ad);

            return card;

        } catch (IOException e) {
            e.printStackTrace();
            VBox fallback = new VBox(5);
            fallback.setPadding(new Insets(10));
            fallback.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-color: #e2e8f0; " +
                            "-fx-border-radius: 8;"
            );
            fallback.getChildren().add(new Text("خطا در بارگذاری کارت"));
            return fallback;
        }
    }
}