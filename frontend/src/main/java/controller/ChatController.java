package controller;

import config.DataReceiver;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import utils.Pages;
import utils.SceneManager;

import java.util.UUID;

/**
 * Controller for the chat page.
 * Displays a chat conversation between the current user and the advertisement owner.
 */
public class ChatController implements DataReceiver {

    @FXML private ListView<String> chatListView;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageField;
    @FXML private Text chatTitle;

    private UUID advId;

    /**
     * Receives the advertisement ID from the previous page.
     *
     * @param data the advertisement UUID
     */
    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID uuid) {
            this.advId = uuid;
            // TODO: Load chat messages based on advId
            chatTitle.setText("گفت‌وگو درباره آگهی #" + advId.toString().substring(0, 8));
        }
    }

    /** Sends a message. */
    @FXML
    public void onSendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        // TODO: Send message via API
        System.out.println("Send message: " + message);
        messageField.clear();
    }

    /**
     * Navigates back to the advertisement detail page if advId is known,
     * otherwise goes to the dashboard.
     */
    @FXML
    public void goBack() {
        if (advId != null) {
            SceneManager.showPage(Pages.AD_DETAIL, null, advId);
        } else {
            SceneManager.showPage(Pages.DASHBOARD, null);
        }
    }
}