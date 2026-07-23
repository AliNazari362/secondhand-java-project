package component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.response.AdvertisementSummaryDto;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller for the advertisement card component.
 * Displays a summary of an advertisement in list/search views.
 * Clicking on the card navigates to the advertisement detail page.
 */
public class AdCardController {

    @FXML
    private Text titleText;
    @FXML
    private Label priceLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private Label statusLabel;

    private AdvertisementSummaryDto adv;

    /**
     * Populates the card with advertisement data including fields, status, and image.
     *
     * @param ad the advertisement summary data to display
     */
    private void setData(AdvertisementSummaryDto ad) {
        this.adv = ad;
        fillElement(ad);
        setStatus(ad);
        setImage(ad);
    }

    /**
     * Loads and sets the advertisement image, falling back to a placeholder if unavailable.
     *
     * @param ad the advertisement summary containing the image path
     */
    private void setImage(AdvertisementSummaryDto ad) {
        String imagePath;
        if (ad.getFirstImagePath() != null && !ad.getFirstImagePath().isEmpty()) {
            imagePath = ad.getFirstImagePath();
            if (!Files.exists(Path.of(imagePath))) {
                String serverPath = imagePath.startsWith("uploads/") ? imagePath : "uploads/" + imagePath;
                imagePath = Utils.BASE_IMAGE_URL + serverPath;
            }
        } else imagePath = ("/images/placeholder.png");

        Image image = new Image(imagePath, 180, 120, true, true);
        if (!image.isError()) {
            imageView.setImage(image);
            imageView.setVisible(true);
        } else imageView.setVisible(false);
    }

    /**
     * Fills the card's text fields with advertisement details.
     *
     * @param ad the advertisement summary containing title, price, city, date, and category
     */
    private void fillElement(AdvertisementSummaryDto ad) {
        titleText.setText(ad.getFullName() != null ? ad.getFullName() : "بدون عنوان");
        priceLabel.setText(ad.getPrice() != null ? Utils.formatPrice(ad.getPrice()) : "قیمت: توافقی");
        cityLabel.setText(ad.getCity() != null ? ad.getCity().getPersianName() : "نامشخص");
        dateLabel.setText(ad.getCreationDate() != null ? ad.getCreationDate().format(Utils.FORMATTER) : "");

        if (ad.getCategoryName() != null && !ad.getCategoryName().isEmpty()) {
            categoryLabel.setText(ad.getCategoryName());
            categoryLabel.setVisible(true);
        } else {
            categoryLabel.setVisible(false);
        }

    }

    /**
     * Sets the status label visibility and text based on the advertisement status.
     *
     * @param ad the advertisement summary containing the status
     */
    private void setStatus(AdvertisementSummaryDto ad) {
        boolean isStatusExist = ad.getStatus() != null;
        statusLabel.setVisible(isStatusExist);
        if (isStatusExist) {
            String statusText = switch (ad.getStatus()) {
                case ACTIVE -> "فعال";
                case PENDING -> "در انتظار بررسی";
                case REJECTED -> "رد شده";
                case SOLD -> "فروخته شده";
                case DELETED -> "حذف شده";
            };
            statusLabel.setText(statusText);
        }
    }

    /**
     * Creates a new advertisement card component from the FXML layout.
     *
     * @param ad the advertisement summary data to populate the card with
     * @return the Vbox containing the rendered ad card, or a fallback error card on failure
     */
    public static VBox createAdCard(AdvertisementSummaryDto ad) {
        try {
            FXMLLoader loader = new FXMLLoader(AdCardController.class.getResource("/fxml/components/ad-card.fxml"));
            VBox card = loader.load();

            AdCardController controller = loader.getController();
            controller.setData(ad);

            return card;

        } catch (IOException e) {
            VBox fallback = new VBox(5);
            fallback.setPadding(new Insets(10));
            fallback.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-color: #e2e8f0; " +
                            "-fx-border-radius: 8;"
            );
            fallback.getChildren().add(new Text("خطا در بارگذاری کارت"));
            return fallback;
        }
    }

    /**
     * Handles the card click event by navigating to the advertisement detail page.
     * Shows an error alert if the advertisement ID is not available.
     */
    @FXML
    public void handleCardClick() {
        if (adv != null) {
            SceneManager.showPage(Pages.AD_DETAIL, adv.getFullName(), adv.getId());
        } else {
            AlertUtil.showError("شناسه آگهی موجود نیست.");
        }
    }
}