package controller;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import model.response.AdvertisementSummaryDto;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class FavoritesController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "علاقه مندی ها");

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        ListView<AdvertisementSummaryDto> favoritesListView = new ListView<>();
        favoritesListView.getStyleClass().add("list-view");
        favoritesListView.setPrefHeight(400);
        favoritesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AdvertisementSummaryDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellBox = new VBox(3);
                    Label titleLabel = new Label(item.getFullName());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label infoLabel = new Label(
                            item.getCity() + " - " + item.getOwnerFullName() + " - " + item.getStatus()
                    );
                    infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

                    cellBox.getChildren().addAll(titleLabel, infoLabel);
                    setGraphic(cellBox);
                }
            }
        });


        favoritesListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                AdvertisementSummaryDto adv = favoritesListView.getSelectionModel().getSelectedItem();
                if (adv != null) SceneManager.showAdDetailPage(adv.getId());
            }
        });

        card.getChildren().add(favoritesListView);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(FavoritesController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}