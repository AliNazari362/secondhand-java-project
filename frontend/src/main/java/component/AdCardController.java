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

    // شناسه آگهی برای انتقال به صفحه جزئیات
    private UUID adId;

    // آدرس پایه سرور برای تصاویر
    private static final String BASE_IMAGE_URL = "http://localhost:8080/";

    /**
     * Sets the advertisement data to be displayed on the card.
     *
     * @param ad the advertisement summary DTO
     */
    public void setData(AdvertisementSummaryDto ad) {
        try {
            // ذخیره شناسه آگهی برای کلیک
            this.adId = ad.getId();

            titleText.setText(ad.getFullName() != null ? ad.getFullName() : "بدون عنوان");

            if (ad.getPrice() != null) {
                priceLabel.setText(formatPrice(ad.getPrice()));
            } else {
                priceLabel.setText("قیمت: توافقی");
            }

            cityLabel.setText(ad.getCity() != null ? ad.getCity().getPersianName() : "نامشخص");
            dateLabel.setText(ad.getCreationDate() != null
                    ? ad.getCreationDate().toLocalDate().toString()
                    : "");

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

                // ۱. بررسی فایل محلی
                File file = new File(imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString(), 180, 120, true, true);
                }

                // ۲. بارگذاری از سرور
                if (image == null || image.isError()) {
                    String serverPath = imagePath;
                    if (!serverPath.startsWith("uploads/") && !serverPath.startsWith("http")) {
                        serverPath = "uploads/" + serverPath;
                    }
                    if (!serverPath.startsWith("http")) {
                        serverPath = BASE_IMAGE_URL + serverPath;
                    }
                    image = new Image(serverPath, 180, 120, true, true);
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

        } catch (Exception e) {
            System.err.println("خطا در نمایش آگهی: " + e.getMessage());
        }
    }

    /**
     * Handles click on the ad card.
     * Navigates to the advertisement detail page.
     */
    @FXML
    public void handleCardClick() {
        if (adId != null) {
            SceneManager.showPage(Pages.AD_DETAIL, null, adId);
        } else {
            AlertUtil.showError("شناسه آگهی موجود نیست.");
        }
    }

    /**
     * Formats a BigDecimal price to a readable Persian string.
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }
}