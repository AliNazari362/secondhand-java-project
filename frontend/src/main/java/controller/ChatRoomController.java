package controller;

import exception.ExceptionHandler;
import javafx.scene.control.*;
import model.request.MessageRequest;
import model.response.MessageResponseDto;
import service.ApiClient;
import utils.AlertUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.UUID;

public class ChatRoomController {

    public static VBox getRoot(UUID chatroomId) {
        return createRoot(chatroomId);
    }

    private static VBox createRoot(UUID chatroomId) {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "گفت و گو");

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        Text chatTitle = new Text("لپ‌تاپ لنوو ThinkPad");
        chatTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        ListView<MessageResponseDto> messageListView = new ListView<>();
        messageListView.getStyleClass().add("list-view");
        messageListView.setPrefHeight(350);
        messageListView.getStyleClass().add("list-view");
        messageListView.setPrefHeight(400);
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessageResponseDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellBox = new VBox(3);
                    Label infoLabel = new Label(item.getSender() + ":" + item.getText());
                    infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

                    cellBox.getChildren().addAll(infoLabel);
                    setGraphic(cellBox);
                }
            }
        });

        HBox sendBox = new HBox(10);
        sendBox.setAlignment(Pos.CENTER);

        TextField messageField = new TextField();
        messageField.setPromptText("پیام خود را بنویسید...");
        messageField.getStyleClass().add("input-field");
        HBox.setHgrow(messageField, javafx.scene.layout.Priority.ALWAYS);

        Button sendBtn = new Button("📤 ارسال");
        sendBtn.getStyleClass().add("primary-btn");
        sendBtn.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (text.isEmpty()) {
                AlertUtil.showWarning("لطفاً پیام بنویسید.");
                return;
            }
            try {
                MessageRequest request = new MessageRequest(text);
                ApiClient.post("/chats/" + chatroomId + "/send-meassges/", request);
            } catch (Exception ex) {
                ExceptionHandler.handle(ex);
            }
            // TODO need to restart this section with local Loding...
            AlertUtil.showSuccess("پیام ارسال شد!");
        });

        sendBox.getChildren().addAll(messageField, sendBtn);

        card.getChildren().addAll(chatTitle, messageListView, sendBox);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(ChatRoomController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}