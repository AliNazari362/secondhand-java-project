package controller;

import component.AdCardController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.response.AdvertisementSummaryDto;
import service.FavoriteService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the favorites page.
 * Displays all advertisements that the user has added to their favorites.
 */
public class FavoritesController {

    @FXML private FlowPane favoritesFlowPane;

    private final FavoriteService favoriteService = new FavoriteService();

    /**
     * Initializes the controller. Loads and displays favorite advertisements.
     */
    @FXML
    public void initialize() {
        loadFavorites();
    }

    /**
     * Loads favorite advertisements from the backend and displays them.
     */
    private void loadFavorites() {
        try {
            List<AdvertisementSummaryDto> favorites = favoriteService.getFavorites();

            Platform.runLater(() -> {
                favoritesFlowPane.getChildren().clear();

                if (favorites == null || favorites.isEmpty()) {
                    showEmptyMessage();
                    return;
                }

                for (AdvertisementSummaryDto ad : favorites) {
                    VBox card = createAdCard(ad);
                    favoritesFlowPane.getChildren().add(card);
                }
            });

        } catch (Exception e) {
            AlertUtil.showError("خطا در بارگذاری علاقه‌مندی‌ها: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows an empty state message when there are no favorites.
     */
    private void showEmptyMessage() {
        VBox emptyMessage = new VBox();
        emptyMessage.setAlignment(javafx.geometry.Pos.CENTER);
        emptyMessage.setPadding(new Insets(50));
        Text message = new Text("هیچ آگهی در لیست علاقه‌مندی‌های شما وجود ندارد.");
        message.setStyle("-fx-font-size: 18px; -fx-fill: #a0aec0;");
        emptyMessage.getChildren().add(message);
        favoritesFlowPane.getChildren().add(emptyMessage);
    }

    /**
     * Creates an ad card component for a favorite advertisement.
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

    /**
     * Navigates back to the advertisement list.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.LIST_ADS, null);
    }

    /**
     * Refreshes the favorites list.
     */
    public void refresh() {
        loadFavorites();
    }
}