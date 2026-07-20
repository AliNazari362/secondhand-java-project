//package controller;
//
//import component.AdCardController;
//import model.response.AdvertisementSummaryDto;
//import service.FavoriteService;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.VBox;
//
//import java.io.IOException;
//import java.util.List;
//
//public class FavoritesController {
//
//    @FXML private FlowPane favoritesFlowPane;
//
//    private final FavoriteService favoriteService = new FavoriteService();
//
//    @FXML
//    public void initialize() {
//        loadFavorites();
//    }
//
//    private void loadFavorites() {
//        try {
//            List<AdvertisementSummaryDto> favorites = favoriteService.getFavorites();
//            favoritesFlowPane.getChildren().clear();
//
//            for (AdvertisementSummaryDto ad : favorites) {
//                VBox card = createAdCard(ad);
//                favoritesFlowPane.getChildren().add(card);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            utils.AlertUtil.showError("خطا در بارگذاری علاقه‌مندی‌ها: " + e.getMessage());
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
//            card.setOnMouseClicked(e -> SceneManager.showAdDetailPage(ad.getId()));
//            return card;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new VBox();
//        }
//    }
//
//    @FXML
//    public void goBack() {
//        SceneManager.showDashboardPage();
//    }
//}