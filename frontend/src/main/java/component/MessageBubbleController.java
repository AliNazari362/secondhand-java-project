package component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MessageBubbleController {

    @FXML private VBox bubbleContainer;
    @FXML private Label senderLabel;
    @FXML private Label messageLabel;
    @FXML private Label timeLabel;

    public void setData(Object message) {
        System.out.println("Set message data called!");
    }
}