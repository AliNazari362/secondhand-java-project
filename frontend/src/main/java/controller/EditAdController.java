package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.UUID;

public class EditAdController {

    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> typeCombo;

    private UUID adId;

    public void setAdId(UUID adId) {
        this.adId = adId;
    }

    @FXML
    public void onUpdate() {
        System.out.println("Update clicked!");
    }

    @FXML
    public void onCancel() {
        System.out.println("Cancel clicked!");
    }
}