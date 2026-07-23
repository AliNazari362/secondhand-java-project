package controller;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.request.UserChangePasswordRequest;
import model.request.UserUpdateRequest;
import model.response.UserDto;
import service.UserService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.SessionManager;

/**
 * Controller for the user profile page.
 * Handles displaying, editing, password change, and account deletion.
 */
public class ProfileController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;

    /**
     * Initializes the controller and loads the current user's profile data.
     */
    @FXML
    public void initialize() {
        loadProfile();
    }

    /**
     * Fetches the current user's profile from the backend and populates the form fields.
     */
    private void loadProfile() {
        try {
            UserDto user = UserService.getProfile();
            Platform.runLater(() -> {
                fullNameField.setText(user.getFullName());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            });
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Updates the user's profile with the values entered in the form.
     * Validates that full name and email are not empty.
     * Updates the session with the new profile information on success.
     */
    @FXML
    public void onUpdateProfile() {
        try {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                AlertUtil.showError("نام کامل و ایمیل نمی توانند خالی باشند.");
                return;
            }

            UserUpdateRequest request = new UserUpdateRequest();
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPhoneNumber(phone.isEmpty() ? null : phone);

            UserDto updated = UserService.updateProfile(request);

            SessionManager.setSession(
                    SessionManager.getToken(),
                    updated.getId(),
                    updated.getFullName(),
                    SessionManager.getRole()
            );

            Platform.runLater(() -> {
                AlertUtil.showSuccess("پروفایل با موفقیت به روزرسانی شد.");
                fullNameField.setText(updated.getFullName());
                emailField.setText(updated.getEmail());
                phoneField.setText(updated.getPhoneNumber() != null ? updated.getPhoneNumber() : "");
            });

        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Changes the user's password after validating the current password.
     * Requires both fields to be filled and the new password to be at least 8 characters.
     */
    @FXML
    public void onChangePassword() {
        try {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                AlertUtil.showError("لطفاً هر دو فیلد رمز عبور را پر کنید.");
                return;
            }

            if (newPassword.length() < 8) {
                AlertUtil.showError("رمز عبور جدید باید حداقل ۸ کاراکتر باشد.");
                return;
            }

            UserChangePasswordRequest request = new UserChangePasswordRequest(currentPassword, newPassword);
            UserService.changePassword(request);

            Platform.runLater(() -> {
                AlertUtil.showSuccess("رمز عبور با موفقیت تغییر کرد.");
                currentPasswordField.clear();
                newPasswordField.clear();
            });

        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Deletes the user's account after confirmation.
     * This is a soft delete operation. Clears the session and navigates to login on success.
     */
    @FXML
    public void onDeleteAccount() {
        boolean confirm = AlertUtil.showConfirmation(
                "آیا از حذف حساب کاربری خود اطمینان دارید؟",
                "این عمل غیرقابل بازگشت است و تمام داده های شما حذف خواهد شد."
        );

        if (!confirm) {
            return;
        }

        try {
            UserService.deleteAccount();
            SessionManager.clear();
            Platform.runLater(() -> {
                AlertUtil.showSuccess("حساب کاربری شما با موفقیت حذف شد.");
                SceneManager.showPage(Pages.LOGIN, null);
            });
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Navigates back to the main dashboard page.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}