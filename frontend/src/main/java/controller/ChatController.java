//package controller;
//
//import component.MessageBubbleController;
//import model.request.ChatroomCreateRequest;
//import model.request.MessageRequest;
//import model.response.ChatroomDetailDto;
//import model.response.ChatroomSummaryDto;
//import model.response.MessageResponseDto;
//import service.ChatService;
//import utils.AlertUtil;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.VBox;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//public class ChatController {
//
//    @FXML private ListView<String> chatListView;
//    @FXML private VBox messagesContainer;
//    @FXML private TextField messageField;
//
//    private final ChatService chatService = new ChatService();
//    private UUID currentChatId;
//    private UUID currentAdId;
//
//    @FXML
//    public void initialize() {
//        loadChatRooms();
//    }
//
//    private void loadChatRooms() {
//        try {
//            List<ChatroomSummaryDto> rooms = chatService.getUserChatRooms();
//            chatListView.getItems().clear();
//            for (ChatroomSummaryDto room : rooms) {
//                chatListView.getItems().add(
//                        room.getAdvTitle() + " - " + room.getMessageCount() + " پیام"
//                );
//            }
//
//            chatListView.setOnMouseClicked(e -> {
//                if (e.getClickCount() == 2) {
//                    int index = chatListView.getSelectionModel().getSelectedIndex();
//                    if (index >= 0) {
//                        // TODO: دریافت chatId از لیست
//                        // currentChatId = rooms.get(index).getId();
//                        // loadMessages(currentChatId);
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری گفت‌وگوها: " + e.getMessage());
//        }
//    }
//
//    private void loadMessages(UUID chatId) {
//        try {
//            ChatroomDetailDto detail = chatService.getChatroomDetail(chatId);
//            messagesContainer.getChildren().clear();
//
//            for (MessageResponseDto msg : detail.getMessages()) {
//                VBox bubble = createMessageBubble(msg);
//                messagesContainer.getChildren().add(bubble);
//            }
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری پیام‌ها: " + e.getMessage());
//        }
//    }
//
//    private VBox createMessageBubble(MessageResponseDto msg) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource("/fxml/components/message-bubble.fxml")
//            );
//            VBox bubble = loader.load();
//            MessageBubbleController controller = loader.getController();
//            controller.setData(msg);
//            return bubble;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new VBox();
//        }
//    }
//
//    @FXML
//    public void onSendMessage() {
//        String text = messageField.getText().trim();
//        if (text.isEmpty()) {
//            AlertUtil.showWarning("لطفاً پیام بنویسید.");
//            return;
//        }
//
//        try {
//            MessageRequest request = new MessageRequest(text);
//            MessageResponseDto response = chatService.sendMessage(currentChatId, request);
//
//            // اضافه کردن پیام به لیست
//            VBox bubble = createMessageBubble(response);
//            messagesContainer.getChildren().add(bubble);
//            messageField.clear();
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در ارسال پیام: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void goBack() {
//        SceneManager.showDashboardPage();
//    }
//}