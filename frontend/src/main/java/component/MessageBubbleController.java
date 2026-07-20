//package component;
//
//import model.response.MessageResponseDto;
//import utils.SessionManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.layout.VBox;
//
//public class MessageBubbleController {
//
//    @FXML private VBox bubbleContainer;
//    @FXML private Label senderLabel;
//    @FXML private Label messageLabel;
//    @FXML private Label timeLabel;
//
//    public void setData(MessageResponseDto msg) {
//        boolean isMine = msg.getSender().getId().equals(SessionManager.getUserId());
//        senderLabel.setText(isMine ? "شما" : msg.getSender().getFullName());
//        messageLabel.setText(msg.getText());
//        timeLabel.setText(msg.getDate().toLocalTime().toString());
//
//        // تغییر استایل برای پیام‌های خودی
//        if (isMine) {
//            bubbleContainer.getStyleClass().add("my-message");
//        } else {
//            bubbleContainer.getStyleClass().add("other-message");
//        }
//    }
//}