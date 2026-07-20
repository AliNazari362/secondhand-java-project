//package controller;
//
//import model.request.UserRegisterRequest;
//import model.response.UserDto;
//import service.AuthService;
//import utils.AlertUtil;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//
//public class RegisterController {
//
//    @FXML private TextField fullNameField;
//    @FXML private TextField emailField;
//    @FXML private PasswordField passwordField;
//    @FXML private TextField phoneField;
//
//    private final AuthService authService = new AuthService();
//
//    @FXML
//    public void handleRegister() {
//        String fullName = fullNameField.getText().trim();
//        String email = emailField.getText().trim();
//        String password = passwordField.getText().trim();
//        String phone = phoneField.getText().trim();
//
//        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
//            AlertUtil.showError("لطفاً همه فیلدهای ضروری را پر کنید.");
//            return;
//        }
//
//        if (password.length() < 8) {
//            AlertUtil.showError("رمز عبور باید حداقل ۸ کاراکتر باشد.");
//            return;
//        }
//
//        try {
//            UserRegisterRequest request = new UserRegisterRequest(
//                    fullName, email, password,
//                    phone.isEmpty() ? null : phone
//            );
//            UserDto response = authService.register(request);
//            AlertUtil.showSuccess("ثبت‌نام موفق! خوش آمدید " + response.getFullName());
//            SceneManager.showLoginPage();
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در ثبت‌نام: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void goToLogin() {
//        SceneManager.showLoginPage();
//    }
//}