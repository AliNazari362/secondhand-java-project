package component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.response.MessageResponseDto;
import utils.SessionManager;
import utils.Utils;

import java.io.IOException;

/**
 * Controller for individual message bubble components in the chat view.
 * Handles layout, styling, and seen status display for sent and received messages.
 */
public class MessageBubbleController {

    @FXML
    private HBox messageRow;
    @FXML
    private VBox messageBubble;
    @FXML
    private VBox bubbleContent;
    @FXML
    private Text messageText;
    @FXML
    private HBox bottomRow;
    @FXML
    private Text timeText;
    @FXML
    private Text statusTicks;

    private static final Object lock = new Object();

    /**
     * Creates a new loader instance for the message bubble FXML.
     * Each call returns a fresh FXMLLoader to ensure independent controller instances.
     *
     * @return a new FXMLLoader configured for the message bubble FXML
     * @throws IOException if the FXML resource cannot be found
     */
    private static FXMLLoader getLoader() throws IOException {
        return new FXMLLoader(MessageBubbleController.class.getResource("/fxml/components/message-bubble.fxml"));
    }

    /**
     * Creates a message bubble HBox for the given message DTO.
     * Used as a static factory method throughout the application.
     *
     * @param message the message data to display
     * @return an HBox containing the rendered message bubble
     */
    public static HBox createMessageBubble(MessageResponseDto message) {
        try {
            FXMLLoader loader = getLoader();
            HBox root = loader.load();

            MessageBubbleController controller = loader.getController();
            controller.initialize(message);

            root.getProperties().put("controller", controller);

            return root;
        } catch (IOException e) {
            return new HBox(new Label("Error loading message"));
        }
    }

    /**
     * Initializes the message bubble with the given message data.
     * Configures alignment, styling, and visibility based on whether the message
     * was sent by the current user or received from another user.
     *
     * @param message the message data to display
     */
    private void initialize(MessageResponseDto message) {
        boolean isMine = message.getSender().getId().equals(SessionManager.getUserId());

        // تنظیم alignment ها
        messageRow.setAlignment(isMine ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        messageRow.setStyle(isMine ? "-fx-padding: 0 40 0 0;" : "-fx-padding: 0 0 0 40;");

        messageBubble.setAlignment(isMine ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        messageBubble.setStyle("-fx-max-width: 320;");  // برگشت به 320 مثل کد قدیمی

        // استایل حباب
        bubbleContent.setStyle(isMine ?
                "-fx-padding: 8 12; -fx-background-color: #bee3f8; -fx-background-radius: 12 12 12 4;" :
                "-fx-padding: 8 12; -fx-background-color: white; -fx-background-radius: 12 12 4 12;");

        // تنظیم متن پیام با wrapping width
        messageText.setText(message.getText());
        messageText.setStyle("-fx-font-size: 13px; -fx-fill: #2d3748;");
        messageText.setWrappingWidth(280);  // قابلیت مهم Text که Label ندارد

        // ردیف پایین: زمان + تیک وضعیت
        bottomRow.setAlignment(isMine ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        // زمان
        timeText.setText(message.getDate().format(Utils.FORMATTER));
        timeText.setStyle("-fx-font-size: 9px; -fx-fill: #a0aec0;");

        // تیک وضعیت (فقط برای پیام‌های خودم)
        if (isMine) {
            statusTicks.setText(message.isSeen() ? "✓✓" : "✓");
            statusTicks.setStyle(message.isSeen() ?
                    "-fx-font-size: 11px; -fx-fill: #3182ce;" :  // آبی - دیده شد
                    "-fx-font-size: 11px; -fx-fill: #a0aec0;");  // خاکستری - هنوز دیده نشده
            statusTicks.setVisible(true);
        } else {
            statusTicks.setVisible(false);
        }
    }

    /**
     * Updates the seen status ticks without rebuilding the entire bubble.
     *
     * @param isSeen true if the message has been seen by the recipient, false otherwise
     */
    public void updateSeenStatus(boolean isSeen) {
        if (statusTicks.isVisible()) {
            statusTicks.setText(isSeen ? "✓✓" : "✓");
            statusTicks.setStyle(isSeen ?
                    "-fx-font-size: 11px; -fx-fill: #3182ce;" :
                    "-fx-font-size: 11px; -fx-fill: #a0aec0;");
        }
    }
}