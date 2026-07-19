package controller;

import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.UUID;

public class FavoritesController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");

        Text title = new Text("❤️ علاقه‌مندی‌ها");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Button backBtn = new Button("🔙 بازگشت");
        backBtn.getStyleClass().add("secondary-btn");
        backBtn.setOnAction(e -> SceneManager.showDashboardPage());

        HBox rightBox = new HBox(backBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, rightBox);

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        ListView<String> favoritesListView = new ListView<>();
        favoritesListView.getStyleClass().add("list-view");
        favoritesListView.setPrefHeight(400);
        favoritesListView.getItems().clear();
        favoritesListView.getItems().addAll(
                "لپ‌تاپ لنوو ThinkPad - ۱۸,۰۰۰,۰۰۰ تومان - تهران",
                "مبل هفت‌نفره - ۱۲,۰۰۰,۰۰۰ تومان - شیراز",
                "پلی‌استیشن ۵ - ۲۵,۰۰۰,۰۰۰ تومان - کرج"
        );
        favoritesListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                SceneManager.showAdDetailPage(UUID.randomUUID());
            }
        });

        card.getChildren().add(favoritesListView);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(FavoritesController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}