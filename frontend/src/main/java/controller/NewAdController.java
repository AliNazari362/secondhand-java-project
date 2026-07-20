//package controller;
//
//import model.enums.City;
//import model.request.ImageRequest;
//import model.request.OptionRequest;
//import model.request.ProductCreateRequest;
//import model.request.ServiceCreateRequest;
//import service.AdService;
//import utils.AlertUtil;
//import utils.ImageUploadUtil;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.stage.FileChooser;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.List;
//
//public class NewAdController {
//
//    @FXML private TextField titleField;
//    @FXML private TextArea descArea;
//    @FXML private TextField priceField;
//    @FXML private ComboBox<String> cityCombo;
//    @FXML private ComboBox<String> typeCombo;
//    @FXML private TextField imagePathField;
//
//    private final AdService adService = new AdService();
//    private String uploadedImagePath;
//
//    @FXML
//    public void initialize() {
//        cityCombo.getItems().addAll("TEHRAN", "ISFAHAN", "SHIRAZ", "MASHHAD", "TABRIZ");
//        typeCombo.getItems().addAll("PRODUCT", "SERVICE");
//    }
//
//    @FXML
//    public void onChooseImage() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().add(
//                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
//        );
//        File file = fileChooser.showOpenDialog(null);
//        if (file != null) {
//            try {
//                byte[] bytes = Files.readAllBytes(file.toPath());
//                uploadedImagePath = ImageUploadUtil.uploadImage(bytes, file.getName());
//                imagePathField.setText(uploadedImagePath);
//                AlertUtil.showSuccess("تصویر با موفقیت آپلود شد.");
//            } catch (Exception e) {
//                AlertUtil.showError("خطا در آپلود تصویر: " + e.getMessage());
//            }
//        }
//    }
//
//    @FXML
//    public void onSubmit() {
//        String title = titleField.getText().trim();
//        String desc = descArea.getText().trim();
//        String price = priceField.getText().trim();
//        String city = cityCombo.getValue();
//        String type = typeCombo.getValue();
//
//        if (title.isEmpty() || desc.isEmpty() || price.isEmpty() || city == null || type == null) {
//            AlertUtil.showError("لطفاً همه فیلدهای ضروری را پر کنید.");
//            return;
//        }
//
//        try {
//            List<ImageRequest> images = new ArrayList<>();
//            if (uploadedImagePath != null) {
//                images.add(new ImageRequest(uploadedImagePath));
//            }
//
//            List<OptionRequest> options = new ArrayList<>();
//            // می‌توانید Optionها را از UI دریافت کنید
//
//            if ("PRODUCT".equals(type)) {
//                ProductCreateRequest request = new ProductCreateRequest(
//                        title, desc, City.valueOf(city), null, null,
//                        null, null, null, null,
//                        new BigDecimal(price), options, images
//                );
//                adService.createProduct(request);
//            } else {
//                ServiceCreateRequest request = new ServiceCreateRequest(
//                        title, desc, City.valueOf(city), null,
//                        null, new BigDecimal(price), null, options, images
//                );
//                adService.createService(request);
//            }
//
//            AlertUtil.showSuccess("آگهی با موفقیت ثبت شد و در انتظار بررسی است.");
//            SceneManager.showDashboardPage();
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در ثبت آگهی: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onCancel() {
//        SceneManager.showDashboardPage();
//    }
//}