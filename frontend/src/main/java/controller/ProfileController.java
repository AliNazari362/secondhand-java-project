package controller;

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

    private final UserService userService = new UserService();

    /**
     * Initializes the controller – loads user profile data from the server.
     */
    @FXML
    public void initialize() {
        loadProfile();
    }

    /**
     * Fetches the current user's profile from the backend and populates the fields.
     */
    private void loadProfile() {
        try {
            System.out.println("🟢 در حال دریافت اطلاعات پروفایل...");
            UserDto user = userService.getProfile();
            System.out.println("🟢 اطلاعات دریافت شد: " + user.getFullName() + " - " + user.getEmail());
            Platform.runLater(() -> {
                fullNameField.setText(user.getFullName());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            });
        } catch (Exception e) {
            System.err.println("🔴 خطا در دریافت پروفایل: " + e.getMessage());
            e.printStackTrace(); // چاپ کامل خطا در کنسول
            AlertUtil.showError("خطا در دریافت اطلاعات پروفایل: " + e.getMessage());
        }
    }

    /**
     * Updates the user's profile with the values entered in the form.
     */
    @FXML
    public void onUpdateProfile() {
        try {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                AlertUtil.showError("نام کامل و ایمیل نمی‌توانند خالی باشند.");
                return;
            }

            UserUpdateRequest request = new UserUpdateRequest();
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPhoneNumber(phone.isEmpty() ? null : phone);

            UserDto updated = userService.updateProfile(request);

            // Update session information
            SessionManager.setSession(
                    SessionManager.getToken(),
                    updated.getId(),
                    updated.getFullName(),
                    SessionManager.getRole()
            );

            Platform.runLater(() -> {
                AlertUtil.showSuccess("پروفایل با موفقیت به‌روزرسانی شد.");
                // Refresh fields with updated values
                fullNameField.setText(updated.getFullName());
                emailField.setText(updated.getEmail());
                phoneField.setText(updated.getPhoneNumber() != null ? updated.getPhoneNumber() : "");
            });

        } catch (Exception e) {
            AlertUtil.showError("خطا در به‌روزرسانی پروفایل: " + e.getMessage());
        }
    }

    /**
     * Changes the user's password after validating the current password.
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
            // No need to store result – just call the service
            userService.changePassword(request);

            Platform.runLater(() -> {
                AlertUtil.showSuccess("رمز عبور با موفقیت تغییر کرد.");
                currentPasswordField.clear();
                newPasswordField.clear();
            });

        } catch (Exception e) {
            AlertUtil.showError("خطا در تغییر رمز عبور: " + e.getMessage());
        }
    }

    /**
     * Deletes the user's account after confirmation.
     * This is a soft delete – the user is marked as DELETED.
     */
    @FXML
    public void onDeleteAccount() {
        boolean confirm = AlertUtil.showConfirmation(
                "آیا از حذف حساب کاربری خود اطمینان دارید؟",
                "این عمل غیرقابل بازگشت است و تمام داده‌های شما حذف خواهد شد."
        );

        if (!confirm) {
            return;
        }

        try {
            userService.deleteAccount();
            SessionManager.clear();
            Platform.runLater(() -> {
                AlertUtil.showSuccess("حساب کاربری شما با موفقیت حذف شد.");
                SceneManager.showPage(Pages.LOGIN, null);
            });
        } catch (Exception e) {
            AlertUtil.showError("خطا در حذف حساب: " + e.getMessage());
        }
    }

    /**
     * Navigates back to the advertisement list.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}