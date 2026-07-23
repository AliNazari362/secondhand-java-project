package controller;

import component.MessageBubbleController;
import config.DataReceiver;
import exception.ApiException;
import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.request.ChatroomCreateRequest;
import model.request.MessageRequest;
import model.response.*;
import service.*;
import utils.*;

import java.util.*;

/**
 * Controller for the individual chat conversation view.
 * Manages real-time messaging with polling, message display via ListView,
 * and seen status updates for sent messages.
 */
public class ChatController implements DataReceiver {

    @FXML private ListView<MessageResponseDto> messageListView;
    @FXML private TextField messageField;
    @FXML private Text chatTitle;
    @FXML private Button submitBtn;

    private AdvertisementDetailDto adv;
    private String chatId;
    private Timer timer;

    private final ObservableList<MessageResponseDto> cachedMessages = FXCollections.observableArrayList();
    private boolean isFirstLoad = true;

    /**
     * Receives data from the previous page. Accepts either a UUID to start/retrieve
     * a chat room or handles the advertisement ID for chat initialization.
     *
     * @param data the data object passed from the previous page
     */
    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID id) {
            handleAdvId(id);
        }
    }

    /**
     * Handles the advertisement ID by attempting to start or retrieve a chat room.
     * Falls back to fetching the chat room directly if the start request returns a 404.
     *
     * @param id the advertisement UUID
     */
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

    /**
     * Initializes the chat UI with the given chat room data.
     * Sets up the ListView, loads initial messages, starts polling, and configures UI elements.
     *
     * @param chat the chat room detail data
     */
    private void localChatData(ChatroomDetailDto chat) {
        Platform.runLater(() -> {
            try {
                adv = AdvService.getAdvDetail(chat.getAdvId().toString());
                chatId = chat.getId().toString();

                setupListView();

                loadInitialMessages();

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

    /**
     * Configures the ListView with a custom cell factory for message bubbles,
     * transparent styling, and auto-scroll behavior when new messages arrive.
     */
    private void setupListView() {
        messageListView.setItems(cachedMessages);

        messageListView.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-control-inner-background: transparent; " +
                        "-fx-border-width: 0; " +
                        "-fx-padding: 15; " +
                        "-fx-selection-bar: transparent; " +
                        "-fx-selection-bar-non-focused: transparent; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent; " +
                        "-fx-cell-size: -1; " +
                        "-fx-vertical-cell-spacing: 0;"
        );
        messageListView.setFocusTraversable(false);

        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessageResponseDto message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-width: 0;");
                } else {
                    HBox bubble = MessageBubbleController.createMessageBubble(message);
                    setGraphic(bubble);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 4 0 4 0; -fx-border-width: 0;");
                    setOpacity(1.0);
                }
            }
        });

        cachedMessages.addListener((ListChangeListener<MessageResponseDto>) change -> Platform.runLater(() -> {
            if (!cachedMessages.isEmpty()) {
                messageListView.scrollTo(cachedMessages.size() - 1);
            }
        }));
    }

    /**
     * Loads the initial set of messages for the current chat room.
     */
    private void loadInitialMessages() {
        try {
            List<MessageResponseDto> messages = ChatService.getMessages(chatId);
            cachedMessages.clear();
            cachedMessages.addAll(messages);
            isFirstLoad = false;
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Polls the server for new messages and updates the message list accordingly.
     * Handles both new message additions and seen status updates for existing messages.
     */
    private void handleMessages() {
        try {
            List<MessageResponseDto> freshMessages = ChatService.getMessages(chatId);
            if (freshMessages.isEmpty()) return;

            if (isFirstLoad) {
                Platform.runLater(() -> {
                    cachedMessages.clear();
                    cachedMessages.addAll(freshMessages);
                    isFirstLoad = false;
                });
                return;
            }

            int lastCachedSize = cachedMessages.size();
            int lastFreshSize = freshMessages.size();

            if (lastFreshSize == lastCachedSize) {
                checkSeenStatus(freshMessages);
                return;
            }

            if (lastFreshSize > lastCachedSize) {
                Platform.runLater(() -> {
                    updateSeenStatus(freshMessages);

                    for (int i = lastCachedSize; i < lastFreshSize; i++) {
                        cachedMessages.add(freshMessages.get(i));
                    }
                });
            }

        } catch (Exception e) {
            // Polling error, silently ignored to prevent log spam
        }
    }

    /**
     * Checks whether the seen status of any sent messages has changed.
     *
     * @param freshMessages the latest messages from the server
     */
    private void checkSeenStatus(List<MessageResponseDto> freshMessages) {
        boolean needsUpdate = false;

        for (int i = 0; i < cachedMessages.size(); i++) {
            MessageResponseDto cached = cachedMessages.get(i);
            MessageResponseDto fresh = freshMessages.get(i);

            if (cached.getSender().getId().equals(SessionManager.getUserId())
                    && cached.isSeen() != fresh.isSeen()) {
                needsUpdate = true;
                break;
            }
        }

        if (needsUpdate) {
            Platform.runLater(() -> updateSeenStatus(freshMessages));
        }
    }

    /**
     * Updates the seen status of cached messages with fresh data from the server.
     * Replaces messages in the ObservableList to trigger UI refresh.
     *
     * @param freshMessages the latest messages from the server
     */
    private void updateSeenStatus(List<MessageResponseDto> freshMessages) {
        for (int i = 0; i < cachedMessages.size(); i++) {
            MessageResponseDto cached = cachedMessages.get(i);
            MessageResponseDto fresh = freshMessages.get(i);

            if (cached.getSender().getId().equals(SessionManager.getUserId())
                    && cached.isSeen() != fresh.isSeen()) {

                cachedMessages.set(i, fresh);
            }
        }
    }

    /**
     * Starts a periodic polling timer to check for new messages every 1.5 seconds.
     */
    private void onlineCheck() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handleMessages();
            }
        }, 1500, 1500);
    }

    /**
     * Sends the typed message to the current chat room.
     * Disables the submit button during the request to prevent double-sending.
     * Clears the input field and refocuses it on success.
     */
    @FXML
    public void onSendMessage() {
        String message = messageField.getText().trim();
        if (message.isBlank() || submitBtn.isDisabled()) return;

        submitBtn.setDisable(true);

        try {
            MessageResponseDto response = ChatService.sendMessage(chatId, new MessageRequest(message));

            cachedMessages.add(response);

            messageField.clear();
            messageField.requestFocus();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        } finally {
            submitBtn.setDisable(false);
        }
    }

    /**
     * Navigates back to the advertisement detail page.
     * Stops the polling timer before leaving the chat view.
     */
    @FXML
    public void goBack() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        SceneManager.showPage(Pages.AD_DETAIL, adv.getFullName(), adv.getId());
    }
}