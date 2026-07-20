package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatController {

    @FXML private ListView<String> chatListView;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageField;
    @FXML private Text chatTitle;

    @FXML
    public void onSendMessage() {
        System.out.println("Send message clicked!");
    }

    @FXML
    public void goBack() {
        System.out.println("Go back clicked!");
    }
}