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

public class DashboardController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> sortCombo;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private FlowPane adFlowPane;

    private final CategoryService categoryService = new CategoryService();
    private List<Category> categories;

    @FXML
    public void initialize() {
        // مقداردهی شهرها
        cityCombo.getItems().add("همه شهرها");
        for (City city : City.values()) {
            cityCombo.getItems().add(city.getPersianName());
        }
        cityCombo.getSelectionModel().selectFirst();

        // مقداردهی مرتب‌سازی
        sortCombo.getItems().addAll("جدیدترین", "قدیمی‌ترین", "ارزان‌ترین", "گران‌ترین", "بالاترین امتیاز");
        sortCombo.getSelectionModel().selectFirst();

        // بارگذاری دسته‌بندی‌ها
        loadCategories();

        // جستجوی اولیه
        performSearch();
    }

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

    @FXML
    public void onSearch() {
        performSearch();
    }

    private void performSearch() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) keyword = null;

            // City
            String cityPersian = cityCombo.getSelectionModel().getSelectedItem();
            City cityEnum = null;
            if (cityPersian != null && !cityPersian.equals("همه شهرها")) {
                cityEnum = City.fromPersianName(cityPersian);
            }

            // CategoryId
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

            // SortBy
            String sortPersian = sortCombo.getSelectionModel().getSelectedItem();
            String sortBy = "newest";
            if (sortPersian != null) {
                switch (sortPersian) {
                    case "جدیدترین": sortBy = "newest"; break;
                    case "قدیمی‌ترین": sortBy = "oldest"; break;
                    case "ارزان‌ترین": sortBy = "priceAsc"; break;
                    case "گران‌ترین": sortBy = "priceDesc"; break;
                    case "بالاترین امتیاز": sortBy = "ratingDesc"; break;
                }
            }

            // MinPrice / MaxPrice
            BigDecimal minPrice = null;
            BigDecimal maxPrice = null;
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = new BigDecimal(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = new BigDecimal(maxPriceField.getText().trim());
            }

            // فراخوانی سرویس با تمام پارامترها
            List<AdvertisementSummaryDto> ads = AdvService.getActiveAds(
                    keyword, cityEnum, categoryId, sortBy, minPrice, maxPrice
            );

            // نمایش در FlowPane
            adFlowPane.getChildren().clear();
            for (AdvertisementSummaryDto ad : ads) {
                VBox card = createAdCard(ad);
                adFlowPane.getChildren().add(card);
            }

            if (ads.isEmpty()) {
                AlertUtil.showWarning("هیچ آگهی با این فیلترها یافت نشد.");
            }

        } catch (Exception e) {
            AlertUtil.showError("خطا در جستجو: " + e.getMessage());
        }
    }

    private VBox createAdCard(AdvertisementSummaryDto ad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/ad-card.fxml"));
            VBox card = loader.load();
            AdCardController controller = loader.getController();
            controller.setData(ad);
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback ساده
            VBox fallback = new VBox(5);
            fallback.setPadding(new Insets(10));
            fallback.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            fallback.getChildren().add(new Text("خطا در بارگذاری کارت"));
            return fallback;
        }
    }
}