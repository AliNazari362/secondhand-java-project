package component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.response.AdvertisementSummaryDto;
import utils.AlertUtil;

import java.io.File;

public class AdCardController {

    @FXML private Text titleText;
    @FXML private Label priceLabel;
    @FXML private Label cityLabel;
    @FXML private Label dateLabel;
    @FXML private Label categoryLabel; // جدید: برای نمایش دسته‌بندی
    @FXML private ImageView imageView; // جدید: برای نمایش تصویر

    /**
     * تنظیم داده‌های آگهی در کارت
     */
    public void setData(AdvertisementSummaryDto ad) {
        try {
            titleText.setText(ad.getFullName());
            priceLabel.setText(formatPrice(ad.getPrice()));
            cityLabel.setText(ad.getCity() != null ? ad.getCity().getPersianName() : "نامشخص");
            dateLabel.setText(ad.getCreationDate() != null ? ad.getCreationDate().toLocalDate().toString() : "");

            // نمایش دسته‌بندی
            if (ad.getCategoryName() != null && !ad.getCategoryName().isEmpty()) {
                categoryLabel.setText("📁 " + ad.getCategoryName());
                categoryLabel.setVisible(true);
            } else {
                categoryLabel.setVisible(false);
            }

            // نمایش تصویر (اگر وجود داشته باشد)
            if (ad.getFirstImagePath() != null && !ad.getFirstImagePath().isEmpty()) {
                File file = new File(ad.getFirstImagePath());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), 200, 150, true, true);
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

    private String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }
}