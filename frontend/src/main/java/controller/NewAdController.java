package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewAdController {

    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> cityCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField imagePathField;

    @FXML
    public void onChooseImage() {
        System.out.println("Choose image clicked!");
    }

    @FXML
    public void onSubmit() {
        System.out.println("Submit clicked!");
    }

    @FXML
    public void onCancel() {
        System.out.println("Cancel clicked!");
    }
}