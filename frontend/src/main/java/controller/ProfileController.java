package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;

    @FXML
    public void onUpdateProfile() {
        System.out.println("Update profile clicked!");
    }

    @FXML
    public void onChangePassword() {
        System.out.println("Change password clicked!");
    }

    @FXML
    public void onDeleteAccount() {
        System.out.println("Delete account clicked!");
    }

    @FXML
    public void goBack() {
        System.out.println("Go back clicked!");
    }
}