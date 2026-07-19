package controller;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import model.response.ChatroomSummaryDto;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class ChatListController {

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "لیست چت ها");

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        ListView<ChatroomSummaryDto> chatListView = new ListView<>();
        chatListView.getStyleClass().add("list-view");
        chatListView.setPrefHeight(400);
        chatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ChatroomSummaryDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellBox = new VBox(3);
                    Label infoLabel = new Label(item.getAdvTitle());
                    infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");
                    cellBox.getChildren().addAll(infoLabel);
                    setGraphic(cellBox);
                }
            }
        });


        chatListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ChatroomSummaryDto chatroom = chatListView.getSelectionModel().getSelectedItem();
                if (chatroom != null) SceneManager.showAdDetailPage(chatroom.getId());
            }
        });

        card.getChildren().add(chatListView);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(ChatListController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}