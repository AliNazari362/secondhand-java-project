//package controller;
//
//import component.AdCardController;
//import model.response.AdvertisementSummaryDto;
//import service.AdService;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.VBox;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//public class DashboardController {
//
//    @FXML private TextField searchField;
//    @FXML private FlowPane adFlowPane;
//
//    private final AdService adService = new AdService();
//
//    @FXML
//    public void initialize() {
//        loadAds(null, null);
//    }
//
//    @FXML
//    public void onSearch() {
//        String keyword = searchField.getText().trim();
//        loadAds(keyword.isEmpty() ? null : keyword, null);
//    }
//
//    private void loadAds(String keyword, String city) {
//        try {
//            List<AdvertisementSummaryDto> ads = adService.getActiveAds(keyword, city);
//            adFlowPane.getChildren().clear();
//
//            for (AdvertisementSummaryDto ad : ads) {
//                VBox card = createAdCard(ad);
//                adFlowPane.getChildren().add(card);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            utils.AlertUtil.showError("خطا در بارگذاری آگهی‌ها: " + e.getMessage());
//        }
//    }
//
//    private VBox createAdCard(AdvertisementSummaryDto ad) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/components/ad-card.fxml")
//            );
//            VBox card = loader.load();
//            AdCardController controller = loader.getController();
//            controller.setData(ad);
//            // کلیک روی کارت برای رفتن به جزئیات
//            card.setOnMouseClicked(e -> SceneManager.showAdDetailPage(ad.getId()));
//            return card;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new VBox();
//        }
//    }
//}