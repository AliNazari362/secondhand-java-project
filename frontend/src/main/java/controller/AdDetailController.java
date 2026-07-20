package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.UUID;

public class AdDetailController {

    @FXML private Text titleText;
    @FXML private Label statusLabel;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label ownerLabel;
    @FXML private Label dateLabel;
    @FXML private Text descText;
    @FXML private Label ratingLabel;
    @FXML private Button chatBtn;
    @FXML private Button favBtn;
    @FXML private Button rateBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button soldBtn;

    private UUID adId;

    public void setAdId(UUID adId) {
        this.adId = adId;
    }

    @FXML
    public void onChat() {
        System.out.println("Chat clicked!");
    }

    @FXML
    public void onAddFavorite() {
        System.out.println("Add favorite clicked!");
    }

    @FXML
    public void onRate() {
        System.out.println("Rate clicked!");
    }

    @FXML
    public void onEdit() {
        System.out.println("Edit clicked!");
    }

    @FXML
    public void onDelete() {
        System.out.println("Delete clicked!");
    }

    @FXML
    public void onMarkAsSold() {
        System.out.println("Mark as sold clicked!");
    }

    @FXML
    public void goBack() {
        System.out.println("Go back clicked!");
    }
}