package controller;

import config.DataReceiver;
import exception.ApiException;
import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.request.ChatroomCreateRequest;
import model.request.MessageRequest;
import model.response.*;
import service.*;
import utils.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatController implements DataReceiver {

    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox messagesContainer;
    @FXML
    private TextField messageField;
    @FXML
    private Text chatTitle;
    @FXML
    private Button submitBtn;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM HH:mm").withLocale(Locale.forLanguageTag("fa-IR"));
    private AdvertisementDetailDto adv;
    private String chatId;

    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID id) {
            handleAdvId(id);
        }
    }

    private void handleAdvId(UUID id) {
        try {
            AdvService.getAdvDetail(id.toString());
            ChatroomCreateRequest request = new ChatroomCreateRequest(id);
            ChatroomDetailDto chat = ChatService.startOrGetChat(request);
            localChatData(chat);
        } catch (Exception e) {
            if (e instanceof ApiException apiEx) {
                if (apiEx.getStatusCode() == 404) {
                    try {
                        ChatroomDetailDto chat = ChatService.getChatroomDetail(id.toString());
                        localChatData(chat);
                    } catch (Exception ex) {
                        ExceptionHandler.handle(e);
                    }
                } else ExceptionHandler.handle(e);
            } else ExceptionHandler.handle(e);
        }
    }

    private void localChatData(ChatroomDetailDto chat) {
        Platform.runLater(() -> {
            try {
                adv = AdvService.getAdvDetail(chat.getAdvId().toString());
                chatId = chat.getId().toString();
                // Run interval for checking any messages
                onlineCheck();
                messageField.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ENTER) onSendMessage();
                });
                chatTitle.setText(adv.getFullName());
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        });
    }

    private void handleMessages() {
        List<MessageResponseDto> messages = null;
        try {
            messages = ChatService.getMessages(chatId);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
        if (messages != null) {
            messagesContainer.getChildren().clear();
            for (MessageResponseDto message : messages) {
                messagesContainer.getChildren().add(createMessageBubble(message));
            }
        }
    }

    private HBox createMessageBubble(MessageResponseDto message) {
        boolean isMine = message.getSender().getId().equals(SessionManager.getUserId());

        HBox messageRow = new HBox();
        messageRow.setAlignment(isMine ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        messageRow.setStyle(isMine ? "-fx-padding: 0 40 0 0;" : "-fx-padding: 0 0 0 40;");

        VBox messageBubble = new VBox(2);
        messageBubble.setAlignment(isMine ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        messageBubble.setStyle("-fx-max-width: 320;");

        // Bubble content
        VBox bubbleContent = new VBox(2);
        bubbleContent.setStyle(isMine ?
                "-fx-padding: 8 12; -fx-background-color: #bee3f8; -fx-background-radius: 12 12 12 4;" :
                "-fx-padding: 8 12; -fx-background-color: white; -fx-background-radius: 12 12 4 12;");

        Text messageText = new Text(message.getText());
        messageText.setStyle("-fx-font-size: 13px; -fx-fill: #2d3748;");
        messageText.setWrappingWidth(280);

        bubbleContent.getChildren().add(messageText);

        // Bottom row: Time + Status ticks
        HBox bottomRow = new HBox(5);
        bottomRow.setAlignment(isMine ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        // Time label
        Text timeText = new Text(message.getDate().format(FORMATTER));
        timeText.setStyle("-fx-font-size: 9px; -fx-fill: #a0aec0;");

        bottomRow.getChildren().add(timeText);

        // Status ticks (only for my messages)
        if (isMine) {
            Text statusTicks = new Text(message.isSeen() ? "✓✓" : "✓");
            statusTicks.setStyle(message.isSeen() ?
                    "-fx-font-size: 11px; -fx-fill: #3182ce;" :  // آبی - دیده شد
                    "-fx-font-size: 11px; -fx-fill: #a0aec0;");  // خاکستری - هنوز دیده نشده
            bottomRow.getChildren().add(statusTicks);
        }

        messageBubble.getChildren().addAll(bubbleContent, bottomRow);
        messageRow.getChildren().add(messageBubble);

        return messageRow;
    }

    private void onlineCheck() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> handleMessages());
            }
        }, 0, 1000);
    }

    @FXML
    public void initialize() {
        // Auto scroll to bottom when new messages arrive
        messagesContainer.heightProperty()
                .addListener((obs, old, newVal) -> chatScrollPane.setVvalue(1.0));
    }

    @FXML
    public void onSendMessage() {
        String message = messageField.getText().trim();
        if (message.isBlank()) return;
        try {
            MessageResponseDto response = ChatService.sendMessage(chatId, new MessageRequest(message));
            messagesContainer.getChildren().add(createMessageBubble(response));
            messageField.clear();
            chatScrollPane.setVvalue(1.0);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.AD_DETAIL, adv.getFullName(), adv.getId());
    }
}