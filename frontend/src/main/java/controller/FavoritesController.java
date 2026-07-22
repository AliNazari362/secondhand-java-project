package controller;

import component.AdCardController;
import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.response.AdvertisementSummaryDto;
import service.FavoriteService;
import utils.Pages;
import utils.SceneManager;

import java.util.List;

/**
 * Controller for the favorites page.
 * Displays all advertisements that the user has added to their favorites.
 */
public class FavoritesController {

    @FXML
    private FlowPane favoritesFlowPane;

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
            List<AdvertisementSummaryDto> favorites = FavoriteService.getFavorites();

            Platform.runLater(() -> {
                favoritesFlowPane.getChildren().clear();

                if (favorites.isEmpty()) {
                    showEmptyMessage();
                    return;
                }

                for (AdvertisementSummaryDto ad : favorites) {
                    VBox card = AdCardController.createAdCard(ad);
                    favoritesFlowPane.getChildren().add(card);
                }
            });

        } catch (Exception e) {
            ExceptionHandler.handle(e);
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
     * Navigates back to the advertisement list.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}