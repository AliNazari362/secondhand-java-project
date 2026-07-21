package component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.response.AdvertisementSummaryDto;
import utils.AlertUtil;

import java.io.File;
import java.math.BigDecimal;

/**
 * Controller for the advertisement card component.
 * Displays a summary of an advertisement in list/search views.
 * This card shows title, price, city, category, status, and a thumbnail image.
 */
public class AdCardController {

    @FXML private Text titleText;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label dateLabel;
    @FXML private Label categoryLabel;
    @FXML private ImageView imageView;
    @FXML private Label statusLabel;

    /**
     * Sets the advertisement data to be displayed on the card.
     *
     * @param ad the advertisement summary DTO containing all necessary fields
     */
    public void setData(AdvertisementSummaryDto ad) {
        try {
            // عنوان
            titleText.setText(ad.getFullName() != null ? ad.getFullName() : "بدون عنوان");

            // قیمت
            if (ad.getPrice() != null) {
                priceLabel.setText(formatPrice(ad.getPrice()));
            } else {
                priceLabel.setText("قیمت: توافقی");
            }

            // شهر
            cityLabel.setText(ad.getCity() != null ? ad.getCity().getPersianName() : "نامشخص");

            // تاریخ (فقط تاریخ، بدون ساعت)
            dateLabel.setText(ad.getCreationDate() != null
                    ? ad.getCreationDate().toLocalDate().toString()
                    : "");

            // دسته‌بندی
            if (ad.getCategoryName() != null && !ad.getCategoryName().isEmpty()) {
                categoryLabel.setText("📁 " + ad.getCategoryName());
                categoryLabel.setVisible(true);
            } else {
                categoryLabel.setVisible(false);
            }

            // وضعیت (با ترجمه فارسی)
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

            // تصویر (اگر مسیر معتبر باشد)
            if (ad.getFirstImagePath() != null && !ad.getFirstImagePath().isEmpty()) {
                File file = new File(ad.getFirstImagePath());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), 180, 120, true, true);
                    imageView.setImage(image);
                    imageView.setVisible(true);
                } else {
                    imageView.setVisible(false);
                }
            } else {
                imageView.setVisible(false);
            }

        } catch (Exception e) {
            AlertUtil.showError("خطا در نمایش آگهی: " + e.getMessage());
        }
    }

    /**
     * Formats a BigDecimal price to a readable Persian string.
     *
     * @param price the price to format
     * @return formatted price string (e.g., "۱,۰۰۰,۰۰۰ تومان")
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }
}