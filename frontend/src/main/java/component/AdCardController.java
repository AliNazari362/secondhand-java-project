package component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.response.AdvertisementSummaryDto;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Controller for the advertisement card component.
 * Displays a summary of an advertisement in list/search views.
 * Clicking on the card navigates to the advertisement detail page.
 */
public class AdCardController {

    @FXML private Text titleText;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label dateLabel;
    @FXML private Label categoryLabel;
    @FXML private ImageView imageView;
    @FXML private Label statusLabel;

    // شناسه آگهی – این مقدار باید برای هر کارت منحصر‌به‌فرد باشد
    private UUID adId;

    // آدرس پایه سرور برای تصاویر
    private static final String BASE_IMAGE_URL = "http://localhost:8080/";

    /**
     * تنظیم داده‌های آگهی روی کارت.
     * شناسه آگهی (adId) در اینجا ذخیره می‌شود.
     *
     * @param ad شیء خلاصه آگهی از سرور
     */
    public void setData(AdvertisementSummaryDto ad) {
        // ===== ذخیره شناسه آگهی =====
        this.adId = ad.getId();
        System.out.println("🆔 [AdCardController] setData called with adId: " + adId);

        // ===== نمایش اطلاعات =====
        titleText.setText(ad.getFullName() != null ? ad.getFullName() : "بدون عنوان");
        priceLabel.setText(ad.getPrice() != null ? formatPrice(ad.getPrice()) : "قیمت: توافقی");
        cityLabel.setText(ad.getCity() != null ? ad.getCity().getPersianName() : "نامشخص");
        dateLabel.setText(ad.getCreationDate() != null ? ad.getCreationDate().toLocalDate().toString() : "");

        if (ad.getCategoryName() != null && !ad.getCategoryName().isEmpty()) {
            categoryLabel.setText("📁 " + ad.getCategoryName());
            categoryLabel.setVisible(true);
        } else {
            categoryLabel.setVisible(false);
        }

        if (ad.getStatus() != null) {
            String statusText = switch (ad.getStatus()) {
                case ACTIVE -> "فعال";
                case PENDING -> "در انتظار بررسی";
                case REJECTED -> "رد شده";
                case SOLD -> "فروخته شده";
                case DELETED -> "حذف شده";
            };
            statusLabel.setText(statusText);
            statusLabel.setVisible(true);
        } else {
            statusLabel.setVisible(false);
        }

        // ===== نمایش تصویر =====
        if (ad.getFirstImagePath() != null && !ad.getFirstImagePath().isEmpty()) {
            String imagePath = ad.getFirstImagePath();
            Image image = null;
            File file = new File(imagePath);
            if (file.exists()) {
                image = new Image(file.toURI().toString(), 180, 120, true, true);
            }
            if (image == null || image.isError()) {
                String serverPath = imagePath.startsWith("uploads/") ? imagePath : "uploads/" + imagePath;
                image = new Image(BASE_IMAGE_URL + serverPath, 180, 120, true, true);
            }
            if (image != null && !image.isError()) {
                imageView.setImage(image);
                imageView.setVisible(true);
            } else {
                imageView.setVisible(false);
            }
        } else {
            imageView.setVisible(false);
        }
    }

    /**
     * کلیک روی کارت – هدایت به صفحه جزئیات آگهی.
     * این متد توسط FXML با رویداد onMouseClicked فراخوانی می‌شود.
     */
    @FXML
    public void handleCardClick() {
        System.out.println("🖱️ [AdCardController] handleCardClick called, adId: " + adId);
        if (adId != null) {
            // ارسال شناسه آگهی به صفحه جزئیات
            SceneManager.showPage(Pages.AD_DETAIL, null, adId);
        } else {
            AlertUtil.showError("شناسه آگهی موجود نیست.");
        }
    }

    /**
     * فرمت قیمت به صورت فارسی.
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }
}