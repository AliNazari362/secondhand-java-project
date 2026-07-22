package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.response.ChatroomSummaryDto;
import service.ChatService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;

import java.util.List;
import java.util.UUID;

/**
 * Controller for displaying the list of chat rooms.
 * Shows all active conversations with unread count and last activity.
 */
public class ChatListController {

    // ===== FXML Fields =====
    @FXML private VBox chatListContainer;
    @FXML private Text totalChatsText;
    @FXML private Text unreadTotalText;

    // ===== State =====
    private List<ChatroomSummaryDto> chatRooms;

    @FXML
    public void initialize() {
        loadChatRooms();
    }

    // ================================
    //  Load Chat Rooms
    // ================================

    private void loadChatRooms() {
        try {
            chatRooms = ChatService.getUserChatRooms();
            Platform.runLater(this::displayChatRooms);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void displayChatRooms() {
        if (chatListContainer == null) return;
        chatListContainer.getChildren().clear();

        if (chatRooms == null || chatRooms.isEmpty()) {
            displayEmptyState();
            return;
        }

        // Update summary texts
        int totalChats = chatRooms.size();
        long totalUnread = chatRooms.stream()
                .mapToLong(ChatroomSummaryDto::getUnreadCount)
                .sum();

        if (totalChatsText != null) {
            totalChatsText.setText("تعداد چت‌ها: " + totalChats);
        }
        if (unreadTotalText != null) {
            unreadTotalText.setText("پیام‌های خوانده نشده: " + totalUnread);
        }

        // Display each chat room
        for (ChatroomSummaryDto chatroom : chatRooms) {
            VBox chatCard = createChatCard(chatroom);
            chatListContainer.getChildren().add(chatCard);
        }
    }

    private void displayEmptyState() {
        VBox emptyBox = new VBox(20);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(50));
        emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Text emptyIcon = new Text("💬");
        emptyIcon.setStyle("-fx-font-size: 48px;");

        Text emptyTitle = new Text("هیچ چتی وجود ندارد");
        emptyTitle.setStyle("-fx-font-size: 18px; -fx-fill: #4a5568; -fx-font-weight: bold;");

        Text emptyDesc = new Text("شما هنوز هیچ گفتگویی را شروع نکرده‌اید");
        emptyDesc.setStyle("-fx-font-size: 14px; -fx-fill: #a0aec0;");

        Button browseAdsBtn = new Button("مشاهده آگهی‌ها");
        browseAdsBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        browseAdsBtn.setOnAction(e -> goBack());

        emptyBox.getChildren().addAll(emptyIcon, emptyTitle, emptyDesc, browseAdsBtn);
        chatListContainer.getChildren().add(emptyBox);
    }

    // ================================
    //  Create Chat Card
    // ================================

    private VBox createChatCard(ChatroomSummaryDto chatroom) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: #e2e8f0; -fx-border-radius: 10; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);");

        // Add hover effect
        card.setOnMouseEntered(e ->
                card.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 10; " +
                        "-fx-border-color: #cbd5e0; -fx-border-radius: 10; " +
                        "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);")
        );
        card.setOnMouseExited(e ->
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-border-color: #e2e8f0; -fx-border-radius: 10; " +
                        "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);")
        );

        // Top row: Ad title + Unread badge
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Text adIcon = new Text("📋");
        adIcon.setStyle("-fx-font-size: 20px;");

        Text adTitle = new Text(chatroom.getAdvTitle() != null ?
                chatroom.getAdvTitle() : "آگهی بدون عنوان");
        adTitle.setStyle("-fx-font-size: 16px; -fx-fill: #2d3748; -fx-font-weight: bold;");
        HBox.setHgrow(adTitle, Priority.ALWAYS);

        // Unread badge
        HBox unreadBadge = null;
        if (chatroom.getUnreadCount() > 0) {
            unreadBadge = new HBox(5);
            unreadBadge.setAlignment(Pos.CENTER);
            unreadBadge.setPadding(new Insets(4, 10, 4, 10));
            unreadBadge.setStyle("-fx-background-color: #fc8181; -fx-background-radius: 15;");

            Text unreadCount = new Text(String.valueOf(chatroom.getUnreadCount()));
            unreadCount.setStyle("-fx-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");

            Text unreadLabel = new Text("خوانده نشده");
            unreadLabel.setStyle("-fx-fill: white; -fx-font-size: 11px;");

            unreadBadge.getChildren().addAll(unreadCount, unreadLabel);
        }

        topRow.getChildren().add(adIcon);
        topRow.getChildren().add(adTitle);
        if (unreadBadge != null) {
            topRow.getChildren().add(unreadBadge);
        }

        // Bottom row: Stats + Action button
        HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        // Message count
        HBox messageCountBox = new HBox(5);
        messageCountBox.setAlignment(Pos.CENTER_LEFT);
        Text messageIcon = new Text("✉️");
        messageIcon.setStyle("-fx-font-size: 14px;");
        Text messageCount = new Text(chatroom.getMessageCount() + " پیام");
        messageCount.setStyle("-fx-font-size: 13px; -fx-fill: #718096;");
        messageCountBox.getChildren().addAll(messageIcon, messageCount);

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Open chat button
        Button openChatBtn = new Button("مشاهده چت");
        openChatBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 8; " +
                "-fx-cursor: hand;");
        openChatBtn.setOnAction(e -> openChat(chatroom.getId()));

        bottomRow.getChildren().addAll(messageCountBox, spacer, openChatBtn);

        card.getChildren().addAll(topRow, bottomRow);

        // Make entire card clickable
        card.setOnMouseClicked(e -> openChat(chatroom.getId()));

        return card;
    }

    // ================================
    //  Actions
    // ================================

    private void openChat(UUID chatroomId) {
        if (chatroomId == null) {
            AlertUtil.showError("شناسه چت نامعتبر است.");
            return;
        }
        SceneManager.showPage(Pages.CHAT, null, chatroomId);
    }

    @FXML
    public void onRefresh() {
        loadChatRooms();
    }

    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}