package controller;

import utils.AlertUtil;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");

        Text title = new Text("💬 گفت‌وگو");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Button backBtn = new Button("🔙 بازگشت");
        backBtn.getStyleClass().add("secondary-btn");
        backBtn.setOnAction(e -> SceneManager.showChatListPage());

        HBox rightBox = new HBox(backBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, rightBox);

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        Text chatTitle = new Text("لپ‌تاپ لنوو ThinkPad");
        chatTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        ListView<String> messageListView = new ListView<>();
        messageListView.getStyleClass().add("list-view");
        messageListView.setPrefHeight(350);
        messageListView.getItems().clear();
        messageListView.getItems().addAll(
                "علی رضایی: سلام، قیمت لپ‌تاپ قابل مذاکره است؟",
                "شما: سلام، بله کمی قابل مذاکره است.",
                "علی رضایی: چند ماه از خریدش می‌گذره؟",
                "شما: حدود ۸ ماه"
        );

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
            messageListView.getItems().add("شما: " + text);
            messageField.clear();
            AlertUtil.showSuccess("پیام ارسال شد!");
        });

        sendBox.getChildren().addAll(messageField, sendBtn);

        card.getChildren().addAll(chatTitle, messageListView, sendBox);
        content.getChildren().add(card);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(ChatRoomController.class.getResource("/style.css").toExternalForm());

        return mainBox;
    }
}