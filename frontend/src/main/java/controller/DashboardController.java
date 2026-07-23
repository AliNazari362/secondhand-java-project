package controller;

import component.AdCardController;
import exception.ExceptionHandler;
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
 * keyword search, category filter, city filter, price range, and sorting.
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
     * Initializes the dashboard controller.
     * Sets up city and sort combo boxes, loads categories, and performs an initial search.
     */
    @FXML
    public void initialize() {
        cityCombo.getItems().add("همه شهرها");
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }
        cityCombo.getSelectionModel().selectFirst();

        sortCombo.getItems().addAll(
                "جدید ترین",
                "قدیمی ترین",
                "ارزان ترین",
                "گران ترین",
                "بالاترین امتیاز"
        );
        sortCombo.getSelectionModel().selectFirst();

        loadCategories();

        performSearch();
    }

    /**
     * Loads leaf categories from the server and populates the category combo box.
     * Each item is displayed with its name and type.
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
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Handles the search button click event.
     */
    @FXML
    public void onSearch() {
        performSearch();
    }

    /**
     * Performs the actual search with all currently selected filters.
     * Gathers keyword, city, category, sort order, and price range,
     * then queries the server and displays results.
     */
    private void performSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) keyword = null;

            String cityPersian = cityCombo.getSelectionModel().getSelectedItem();
            City cityEnum = null;
            if (cityPersian != null && !cityPersian.equals("همه شهرها")) {
                cityEnum = City.fromPersianName(cityPersian);
            }

            String categoryDisplay = categoryCombo.getSelectionModel().getSelectedItem();
            Long categoryId = null;
            if (categoryDisplay != null && !categoryDisplay.equals("همه دسته بندی ها")) {
                categoryId = categoryNameToIdMap.get(categoryDisplay);
            }

            String sortBy = getSort();

            BigDecimal minPrice = null;
            BigDecimal maxPrice = null;
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = new BigDecimal(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = new BigDecimal(maxPriceField.getText().trim());
            }

            List<AdvertisementSummaryDto> ads = AdvService.getActiveAds(
                    keyword,
                    cityEnum,
                    categoryId,
                    sortBy,
                    minPrice,
                    maxPrice
            );

            displayAds(ads);

        } catch (NumberFormatException e) {
            AlertUtil.showError("لطفا قیمت را به صورت عدد وارد کنید.");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Maps the Persian sort option to the corresponding API sort parameter.
     *
     * @return the sort parameter string for the API call
     */
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
     * Shows an empty state message if no results are found.
     *
     * @param ads the list of advertisement summaries to display
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