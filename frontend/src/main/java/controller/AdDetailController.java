//package controller;
//
//import model.response.AdvertisementDetailDto;
//import service.AdService;
//import service.RatingService;
//import utils.AlertUtil;
//import utils.SceneManager;
//import utils.SessionManager;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.text.Text;
//
//import java.util.UUID;
//
//public class AdDetailController {
//
//    @FXML private Text titleText;
//    @FXML private Label statusLabel;
//    @FXML private Label priceLabel;
//    @FXML private Label cityLabel;
//    @FXML private Label ownerLabel;
//    @FXML private Label dateLabel;
//    @FXML private Text descText;
//    @FXML private Label ratingLabel;
//
//    @FXML private Button chatBtn;
//    @FXML private Button favBtn;
//    @FXML private Button rateBtn;
//    @FXML private Button editBtn;
//    @FXML private Button deleteBtn;
//    @FXML private Button soldBtn;
//
//    private UUID adId;
//    private final AdService adService = new AdService();
//    private final RatingService ratingService = new RatingService();
//    private AdvertisementDetailDto currentAd;
//
//    public void setAdId(UUID adId) {
//        this.adId = adId;
//        loadAdDetail();
//    }
//
//    @FXML
//    public void initialize() {
//        // رویدادهای دکمه‌ها در initialize متصل می‌شوند
//    }
//
//    private void loadAdDetail() {
//        try {
//            currentAd = adService.getAdDetail(adId);
//            fillData(currentAd);
//
//            // بررسی مالکیت
//            boolean isOwner = currentAd.getOwner().getId().equals(SessionManager.getUserId());
//            chatBtn.setVisible(!isOwner);
//            editBtn.setVisible(isOwner);
//            deleteBtn.setVisible(isOwner);
//            soldBtn.setVisible(isOwner);
//
//            // دریافت امتیاز
//            double avg = ratingService.getAverageRating(adId);
//            long count = ratingService.getRatingCount(adId);
//            ratingLabel.setText("⭐ " + String.format("%.1f", avg) + " (" + count + " نظر)");
//
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در بارگذاری جزئیات: " + e.getMessage());
//        }
//    }
//
//    private void fillData(AdvertisementDetailDto ad) {
//        titleText.setText(ad.getFullName());
//        statusLabel.setText(ad.getStatus().name());
//        priceLabel.setText("💰 قیمت: " + ad.getProductDetail().getPrice() + " تومان");
//        cityLabel.setText("📍 " + ad.getCity().name());
//        ownerLabel.setText("👤 فروشنده: " + ad.getOwner().getFullName());
//        dateLabel.setText("📅 تاریخ ثبت: " + ad.getCreationDate().toLocalDate());
//        descText.setText(ad.getDescription());
//    }
//
//    @FXML
//    public void onChat() {
//        // TODO: پیاده‌سازی شروع چت
//        AlertUtil.showWarning("صفحه چت در حال توسعه است.");
//    }
//
//    @FXML
//    public void onAddFavorite() {
//        try {
//            new service.FavoriteService().addFavorite(adId);
//            AlertUtil.showSuccess("به علاقه‌مندی‌ها اضافه شد.");
//        } catch (Exception e) {
//            AlertUtil.showError("خطا: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onRate() {
//        // TODO: باز کردن دیالوگ امتیازدهی
//        AlertUtil.showWarning("صفحه امتیازدهی در حال توسعه است.");
//    }
//
//    @FXML
//    public void onEdit() {
//        SceneManager.showEditAdPage(adId);
//    }
//
//    @FXML
//    public void onDelete() {
//        try {
//            adService.deleteAd(adId);
//            AlertUtil.showSuccess("آگهی با موفقیت حذف شد.");
//            SceneManager.showDashboardPage();
//        } catch (Exception e) {
//            AlertUtil.showError("خطا در حذف: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void onMarkAsSold() {
//        try {
//            adService.markAsSold(adId);
//            AlertUtil.showSuccess("آگهی به فروخته‌شده تغییر کرد.");
//            loadAdDetail();
//        } catch (Exception e) {
//            AlertUtil.showError("خطا: " + e.getMessage());
//        }
//    }
//
//    @FXML
//    public void goBack() {
//        SceneManager.showDashboardPage();
//    }
//}