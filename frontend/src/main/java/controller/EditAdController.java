//package controller;
//
//import model.enums.City;
//import model.request.OptionRequest;
//import model.request.ProductUpdateRequest;
//import model.request.ServiceUpdateRequest;
//import model.response.AdvertisementDetailDto;
//import service.AdService;
//import utils.AlertUtil;
//import utils.SceneManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class EditAdController {
//
//    @FXML private TextField titleField;
//    @FXML private TextArea descArea;
//    @FXML private TextField priceField;
//    @FXML private ComboBox<String> cityCombo;
//    @FXML private ComboBox<String> typeCombo;
//
//    private UUID adId;
//    private final AdService adService = new AdService();
//    private AdvertisementDetailDto currentAd;
//
//    public void setAdId(UUID adId) {
//        this.adId = adId;
//        loadAdData();
//    }
//
//    @FXML
//    public void initialize() {
//        cityCombo.getItems().addAll("TEHRAN", "ISFAHAN", "SHIRAZ", "MASHHAD", "TABRIZ");
//        typeCombo.getItems().addAll("PRODUCT", "SERVICE");
//    }
//
//    private void loadAdData() {
//        try {
//            currentAd = adService.getAdDetail(adId);
//            titleField.setText(currentAd.getFullName());
//            descArea.setText(currentAd.getDescription());
//            priceField.setText(currentAd.getProductDetail().getPrice().toString());
//            cityCombo.setValue(currentAd.getCity().name());
//            typeCombo.setValue(currentAd.getAdvType().name());
//            typeCombo.setDisable(true);
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری آگهی: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onUpdate() {
//        String title = titleField.getText().trim();
//        String desc = descArea.getText().trim();
//        String price = priceField.getText().trim();
//        String city = cityCombo.getValue();
//
//        if (title.isEmpty() || desc.isEmpty() || price.isEmpty() || city == null) {
//            AlertUtil.showError("لطفاً همه فیلدها را پر کنید.");
//            return;
//        }
//
//        try {
//            if ("PRODUCT".equals(typeCombo.getValue())) {
//                ProductUpdateRequest request = new ProductUpdateRequest(
//                        title, desc, City.valueOf(city), null,
//                        null, null, null, null, null,
//                        new BigDecimal(price), new ArrayList<>()
//                );
//                adService.updateProduct(adId, request);
//            } else {
//                ServiceUpdateRequest request = new ServiceUpdateRequest(
//                        title, desc, City.valueOf(city), null,
//                        null, new BigDecimal(price), null, new ArrayList<>()
//                );
//                adService.updateService(adId, request);
//            }
//
//            AlertUtil.showSuccess("آگهی با موفقیت ویرایش شد.");
//            SceneManager.showDashboardPage();
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در ویرایش: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onCancel() {
//        SceneManager.showDashboardPage();
//    }
//}