//package controller;
//
//import model.request.UserChangePasswordRequest;
//import model.request.UserUpdateRequest;
//import model.response.UserDto;
//import service.UserService;
//import utils.AlertUtil;
//import utils.SceneManager;
//import utils.SessionManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//
//public class ProfileController {
//
//    @FXML private TextField fullNameField;
//    @FXML private TextField emailField;
//    @FXML private TextField phoneField;
//    @FXML private PasswordField currentPasswordField;
//    @FXML private PasswordField newPasswordField;
//
//    private final UserService userService = new UserService();
//
//    @FXML
//    public void initialize() {
//        loadProfile();
//    }
//
//    private void loadProfile() {
//        try {
//            UserDto user = userService.getProfile();
//            fullNameField.setText(user.getFullName());
//            emailField.setText(user.getEmail());
//            phoneField.setText(user.getPhoneNumber());
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری پروفایل: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onUpdateProfile() {
//        String fullName = fullNameField.getText().trim();
//        String email = emailField.getText().trim();
//        String phone = phoneField.getText().trim();
//
//        try {
//            UserUpdateRequest request = new UserUpdateRequest(
//                    fullName, email, phone.isEmpty() ? null : phone
//            );
//            UserDto updated = userService.updateProfile(request);
//
//            // به‌روزرسانی SessionManager
//            SessionManager.setSession(
//                    SessionManager.getToken(),
//                    SessionManager.getUserId(),
//                    updated.getFullName(),
//                    SessionManager.getRole()
//            );
//
//            AlertUtil.showSuccess("پروفایل با موفقیت ویرایش شد.");
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در ویرایش: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onChangePassword() {
//        String current = currentPasswordField.getText().trim();
//        String newPass = newPasswordField.getText().trim();
//
//        if (current.isEmpty() || newPass.isEmpty()) {
//            AlertUtil.showError("لطفاً هر دو فیلد رمز را پر کنید.");
//            return;
//        }
//
//        if (newPass.length() < 8) {
//            AlertUtil.showError("رمز عبور جدید باید حداقل ۸ کاراکتر باشد.");
//            return;
//        }
//
//        try {
//            UserChangePasswordRequest request = new UserChangePasswordRequest(current, newPass);
//            userService.changePassword(request);
//            AlertUtil.showSuccess("رمز عبور با موفقیت تغییر کرد.");
//            currentPasswordField.clear();
//            newPasswordField.clear();
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در تغییر رمز: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onDeleteAccount() {
//        // TODO: نمایش دیالوگ تأیید
//        try {
//            userService.deleteAccount();
//            SessionManager.clear();
//            AlertUtil.showSuccess("حساب شما با موفقیت حذف شد.");
//            SceneManager.showLoginPage();
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در حذف حساب: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void goBack() {
//        SceneManager.showDashboardPage();
//    }
//}