package controller;

import config.DataReceiver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.response.AdvertisementDetailDto;
import model.response.ImageResponseDto;
import model.response.UserSummaryDto;
import service.AdvService;
import service.FavoriteService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.SessionManager;

import java.io.File;
import java.util.UUID;

/**
 * Controller for the advertisement detail page.
 * <p>
 * Displays full information about an advertisement including images, options,
 * comments, and allows actions like chat, favorites, rating, edit, delete, and mark as sold.
 * All FXML fields are null-checked to prevent NPE.
 * </p>
 *
 * @author [Your Name]
 * @version 1.0
 * @see AdvService
 * @see FavoriteService
 */
public class AdDetailController implements DataReceiver {

    // ==================== FXML Fields ====================
    @FXML private Text titleText;
    @FXML private Label statusLabel;
    @FXML private Text descriptionText;
    @FXML private Text priceText;
    @FXML private Text cityText;
    @FXML private Text addressText;
    @FXML private Text dateText;
    @FXML private Text ownerText;
    @FXML private Text categoryText;
    @FXML private Text ratingValueText;
    @FXML private Text ratingCountText;
    @FXML private FlowPane imagesContainer;
    @FXML private FlowPane optionsContainer;
    @FXML private VBox commentsContainer;
    @FXML private VBox productFields;
    @FXML private VBox serviceFields;
    @FXML private Text productBrandText;
    @FXML private Text productModelText;
    @FXML private Text productStateText;
    @FXML private Text productConstructorText;
    @FXML private Text serviceTypeText;
    @FXML private Text serviceCostText;

    @FXML private Button chatBtn;
    @FXML private Button favBtn;
    @FXML private Button rateBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button soldBtn;
    @FXML private TextArea commentArea;

    // ==================== Fields ====================
    private UUID advId;
    private AdvertisementDetailDto currentAd;
    private boolean isFavorite = false;

    private final AdvService advService = new AdvService();
    private final FavoriteService favoriteService = new FavoriteService();

    // ==================== DataReceiver Implementation ====================

    /**
     * Receives data passed from the previous page (the advertisement ID).
     * Logs the received data for debugging.
     *
     * @param data the advertisement UUID
     */
    @Override
    public void receiveData(Object data) {
        System.out.println("📥 [AdDetailController] receiveData called with: " + data);
        if (data instanceof UUID uuid) {
            this.advId = uuid;
            System.out.println("✅ advId set to: " + advId);
            loadAdDetail();
        } else {
            System.err.println("❌ Data is not UUID: " + data);
            showError("شناسه آگهی نامعتبر است.");
        }
    }

    // ==================== Loading Methods ====================

    /**
     * Loads the full advertisement details from the backend.
     * If the advId is null, shows an error and returns.
     */
    private void loadAdDetail() {
        if (advId == null) {
            showError("شناسه آگهی معتبر نیست.");
            return;
        }

        try {
            System.out.println("🔄 Loading ad detail for ID: " + advId);
            currentAd = AdvService.getAdvDetail(advId.toString());
            Platform.runLater(this::displayAdDetail);
        } catch (Exception e) {
            e.printStackTrace();
            showError("خطا در دریافت اطلاعات آگهی: " + e.getMessage());
        }
    }

    // ==================== Display Methods ====================

    /**
     * Displays the loaded advertisement details in the UI.
     * All FXML fields are null-checked to avoid NullPointerException.
     */
    private void displayAdDetail() {
        if (currentAd == null) {
            System.err.println("❌ currentAd is null, cannot display.");
            return;
        }
        System.out.println("🔥🔥🔥 AdDetailController displayAdDetail() called 🔥🔥🔥");

        try {
            // ===== Basic Information =====
            if (titleText != null) {
                titleText.setText(currentAd.getFullName());
            }

            if (statusLabel != null) {
                statusLabel.setText(translateStatus(currentAd.getStatus().name()));
                statusLabel.getStyleClass().add("status-" + currentAd.getStatus().name().toLowerCase());
            }

            if (descriptionText != null) {
                descriptionText.setText(currentAd.getDescription() != null
                        ? currentAd.getDescription()
                        : "توضیحاتی ثبت نشده است.");
            }

            // ===== Price =====
            if (priceText != null) {
                if (currentAd.getProductDetail() != null) {
                    priceText.setText(formatPrice(currentAd.getProductDetail().getPrice()));
                    priceText.setVisible(true);
                } else if (currentAd.getServiceDetail() != null) {
                    priceText.setText(formatPrice(currentAd.getServiceDetail().getCostOfPart()) +
                            " / " + currentAd.getServiceDetail().getTypeOfPart().name());
                    priceText.setVisible(true);
                } else {
                    priceText.setVisible(false);
                }
            }

            // ===== Location and Date =====
            if (cityText != null) {
                cityText.setText(currentAd.getCity() != null
                        ? currentAd.getCity().getPersianName()
                        : "نامشخص");
            }

            if (addressText != null) {
                addressText.setText(currentAd.getAddress() != null
                        ? currentAd.getAddress()
                        : "آدرسی ثبت نشده است.");
            }

            if (dateText != null) {
                dateText.setText(currentAd.getCreationDate() != null
                        ? currentAd.getCreationDate().toLocalDate().toString()
                        : "");
            }

            if (categoryText != null) {
                categoryText.setText(currentAd.getCategoryName() != null
                        ? currentAd.getCategoryName()
                        : "بدون دسته‌بندی");
            }

            // ===== Owner =====
            UserSummaryDto owner = currentAd.getOwner();
            if (ownerText != null && owner != null) {
                ownerText.setText(owner.getFullName() + " (" + owner.getEmail() + ")");
            }

            // ===== Sections =====
            displayImages();
            displayOptions();
            displayTypeSpecificFields();
            updateActionButtons();
            checkFavoriteStatus();
            displayComments();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("خطا در نمایش آگهی: " + e.getMessage());
        }
    }

    /**
     * Displays images in a horizontal gallery.
     * If no images, shows a placeholder label.
     */
    private void displayImages() {
        if (imagesContainer == null) return;
        imagesContainer.getChildren().clear();

        if (currentAd.getImages() == null || currentAd.getImages().isEmpty()) {
            Label noImageLabel = new Label("تصویری برای این آگهی وجود ندارد.");
            noImageLabel.setStyle("-fx-text-fill: #a0aec0;");
            imagesContainer.getChildren().add(noImageLabel);
            return;
        }

        for (ImageResponseDto imageDto : currentAd.getImages()) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8;");

            try {
                String imagePath = imageDto.getPath();
                File file = new File(imagePath);
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    String serverUrl = "http://localhost:8080/" + imagePath;
                    imageView.setImage(new Image(serverUrl));
                }
            } catch (Exception e) {
                // Ignore individual image load errors
            }

            VBox imageBox = new VBox(5);
            imageBox.setAlignment(Pos.CENTER);
            imageBox.setPadding(new Insets(5));
            imageBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            imageBox.getChildren().add(imageView);
            imagesContainer.getChildren().add(imageBox);
        }
    }

    /**
     * Displays key-value options/attributes of the advertisement.
     * Shows a placeholder if no options exist.
     */
    private void displayOptions() {
        if (optionsContainer == null) return;
        optionsContainer.getChildren().clear();

        if (currentAd.getOptions() == null || currentAd.getOptions().isEmpty()) {
            Label noOptionLabel = new Label("ویژگی اضافی ثبت نشده است.");
            noOptionLabel.setStyle("-fx-text-fill: #a0aec0;");
            optionsContainer.getChildren().add(noOptionLabel);
            return;
        }

        for (var option : currentAd.getOptions()) {
            HBox chip = new HBox(5);
            chip.setAlignment(Pos.CENTER_LEFT);
            chip.setPadding(new Insets(4, 10, 4, 10));
            chip.setStyle("-fx-background-color: #edf2f7; -fx-background-radius: 15;");
            Label keyLabel = new Label(option.getOption() + ":");
            keyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #4a5568;");
            Label valueLabel = new Label(option.getValue());
            valueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2d3748;");
            chip.getChildren().addAll(keyLabel, valueLabel);
            optionsContainer.getChildren().add(chip);
        }
    }

    /**
     * Shows product-specific or service-specific fields.
     * Hides both sections first, then shows the appropriate one.
     */
    private void displayTypeSpecificFields() {
        if (productFields != null) productFields.setVisible(false);
        if (serviceFields != null) serviceFields.setVisible(false);

        if (currentAd.getProductDetail() != null) {
            if (productFields != null) productFields.setVisible(true);
            if (productBrandText != null) {
                productBrandText.setText(currentAd.getProductDetail().getBrand() != null
                        ? currentAd.getProductDetail().getBrand()
                        : "نامشخص");
            }
            if (productModelText != null) {
                productModelText.setText(currentAd.getProductDetail().getModel() != null
                        ? currentAd.getProductDetail().getModel()
                        : "نامشخص");
            }
            if (productStateText != null) {
                productStateText.setText(currentAd.getProductDetail().getStateOfProduct() != null
                        ? currentAd.getProductDetail().getStateOfProduct().getPersianName()
                        : "نامشخص");
            }
            if (productConstructorText != null) {
                productConstructorText.setText(currentAd.getProductDetail().getConstructor() != null
                        ? currentAd.getProductDetail().getConstructor()
                        : "نامشخص");
            }
        } else if (currentAd.getServiceDetail() != null) {
            if (serviceFields != null) serviceFields.setVisible(true);
            if (serviceTypeText != null) {
                serviceTypeText.setText(currentAd.getServiceDetail().getTypeOfPart() != null
                        ? currentAd.getServiceDetail().getTypeOfPart().name()
                        : "نامشخص");
            }
            if (serviceCostText != null) {
                serviceCostText.setText(formatPrice(currentAd.getServiceDetail().getCostOfPart()));
            }
        }
    }

    /**
     * Updates action buttons based on ownership and admin status.
     * - Chat, Favorite, and Rating are visible only for non-owners.
     * - Edit, Delete are visible for owner or admin.
     * - Sold button is visible for owner if status is ACTIVE.
     */
    private void updateActionButtons() {
        UUID currentUserId = SessionManager.getUserId();
        boolean isOwner = currentAd.getOwner() != null &&
                currentAd.getOwner().getId().equals(currentUserId);
        boolean isAdmin = SessionManager.isAdmin();

        if (chatBtn != null) {
            chatBtn.setVisible(!isOwner);
            chatBtn.setManaged(!isOwner);
        }
        if (favBtn != null) {
            favBtn.setVisible(!isOwner);
            favBtn.setManaged(!isOwner);
        }
        if (rateBtn != null) {
            rateBtn.setVisible(!isOwner);
            rateBtn.setManaged(!isOwner);
        }
        if (editBtn != null) {
            editBtn.setVisible(isOwner);
            editBtn.setManaged(isOwner);
        }
        if (deleteBtn != null) {
            deleteBtn.setVisible(isOwner || isAdmin);
            deleteBtn.setManaged(isOwner || isAdmin);
        }
        if (soldBtn != null) {
            soldBtn.setVisible(isOwner && currentAd.getStatus().name().equals("ACTIVE"));
            soldBtn.setManaged(isOwner && currentAd.getStatus().name().equals("ACTIVE"));
        }
    }

    /**
     * Checks if the current advertisement is in the user's favorites.
     * If the user is not logged in, favorite status is set to false.
     * If the API call fails, it gracefully falls back to false.
     */
    private void checkFavoriteStatus() {
        if (!SessionManager.isLoggedIn() || advId == null) {
            isFavorite = false;
            updateFavoriteButton();
            return;
        }

        try {
            isFavorite = favoriteService.isFavorite(advId.toString());
        } catch (Exception e) {
            System.err.println("❌ خطا در بررسی وضعیت علاقه‌مندی: " + e.getMessage());
            isFavorite = false;
        }
        updateFavoriteButton();
    }

    /**
     * Updates the favorite button text and style based on favorite status.
     */
    private void updateFavoriteButton() {
        if (favBtn == null) return;
        if (isFavorite) {
            favBtn.setText("❤️ حذف از علاقه‌مندی");
            favBtn.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold;");
        } else {
            favBtn.setText("🤍 افزودن به علاقه‌مندی");
            favBtn.setStyle("-fx-text-fill: #2d3748;");
        }
    }

    /**
     * Displays comments for the advertisement.
     * Shows a placeholder if no comments exist.
     */
    private void displayComments() {
        if (commentsContainer == null) return;
        commentsContainer.getChildren().clear();

        if (currentAd.getComments() == null || currentAd.getComments().isEmpty()) {
            Label noCommentLabel = new Label("هنوز نظری ثبت نشده است.");
            noCommentLabel.setStyle("-fx-text-fill: #a0aec0;");
            commentsContainer.getChildren().add(noCommentLabel);
            return;
        }

        for (var comment : currentAd.getComments()) {
            VBox commentBox = new VBox(4);
            commentBox.setPadding(new Insets(10));
            commentBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);

            Text authorText = new Text(comment.getAuthor() != null
                    ? comment.getAuthor().getFullName()
                    : "ناشناس");
            authorText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: #2d3748;");

            Text ratingText = new Text("⭐ " + comment.getRate() + "/5");
            ratingText.setStyle("-fx-font-size: 12px; -fx-fill: #f6ad55;");

            Text dateText = new Text(comment.getDate() != null
                    ? comment.getDate().toLocalDate().toString()
                    : "");
            dateText.setStyle("-fx-font-size: 11px; -fx-fill: #a0aec0;");

            header.getChildren().addAll(authorText, ratingText, dateText);

            Text contentText = new Text(comment.getText());
            contentText.setStyle("-fx-font-size: 13px; -fx-fill: #4a5568;");
            contentText.setWrappingWidth(600);

            commentBox.getChildren().addAll(header, contentText);
            commentsContainer.getChildren().add(commentBox);
        }
    }

    // ==================== Action Methods ====================

    /**
     * Opens the chat page with the advertisement ID.
     * Shows an error if advId is null.
     */
    @FXML
    public void onChat() {
        if (advId == null) {
            AlertUtil.showError("شناسه آگهی نامعتبر است.");
            return;
        }
        SceneManager.showPage(Pages.CHAT, null, advId);
    }

    /**
     * Toggles the favorite status of the current advertisement.
     * Shows appropriate messages and updates the button.
     */
    @FXML
    public void onAddFavorite() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب کاربری خود شوید.");
            SceneManager.showPage(Pages.LOGIN, null);
            return;
        }

        if (advId == null) {
            AlertUtil.showError("شناسه آگهی نامعتبر است.");
            return;
        }

        try {
            if (isFavorite) {
                favoriteService.removeFavorite(advId.toString());
                isFavorite = false;
                AlertUtil.showSuccess("آگهی از علاقه‌مندی‌ها حذف شد.");
            } else {
                favoriteService.addFavorite(advId.toString());
                isFavorite = true;
                AlertUtil.showSuccess("آگهی به علاقه‌مندی‌ها اضافه شد.");
            }
            updateFavoriteButton();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("خطا در عملیات علاقه‌مندی: " + e.getMessage());
        }
    }

    /**
     * Shows a placeholder for rating functionality.
     */
    @FXML
    public void onRate() {
        AlertUtil.showWarning("قابلیت امتیازدهی در حال توسعه است.");
    }

    /**
     * Navigates to the edit advertisement page with the current advId.
     */
    @FXML
    public void onEdit() {
        if (advId != null) {
            SceneManager.showPage(Pages.EDIT_AD, null, advId);
        }
    }

    /**
     * Deletes the advertisement after confirmation.
     * Uses AdvService.deleteAdv and navigates back on success.
     */
    @FXML
    public void onDelete() {
        boolean confirm = AlertUtil.showConfirmation(
                "حذف آگهی",
                "آیا از حذف این آگهی اطمینان دارید؟"
        );
        if (!confirm) return;

        try {
            AdvService.deleteAdv(advId.toString());
            AlertUtil.showSuccess("آگهی با موفقیت حذف شد.");
            goBack();
        } catch (Exception e) {
            AlertUtil.showError("خطا در حذف آگهی: " + e.getMessage());
        }
    }

    /**
     * Marks the advertisement as sold after confirmation.
     * Reloads the ad detail after successful update.
     */
    @FXML
    public void onMarkAsSold() {
        boolean confirm = AlertUtil.showConfirmation(
                "تغییر وضعیت به فروخته‌شده",
                "آیا از تغییر وضعیت این آگهی به فروخته‌شده اطمینان دارید؟"
        );
        if (!confirm) return;

        try {
            AdvService.markAsSold(advId.toString());
            AlertUtil.showSuccess("وضعیت آگهی به فروخته‌شده تغییر کرد.");
            loadAdDetail();
        } catch (Exception e) {
            AlertUtil.showError("خطا در تغییر وضعیت: " + e.getMessage());
        }
    }

    /**
     * Navigates back to the dashboard.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }

    // ==================== Utility Methods ====================

    /**
     * Translates an English status code to Persian.
     *
     * @param status the status enum name (e.g., "ACTIVE")
     * @return the Persian translation
     */
    private String translateStatus(String status) {
        return switch (status) {
            case "ACTIVE" -> "فعال";
            case "PENDING" -> "در انتظار بررسی";
            case "REJECTED" -> "رد شده";
            case "SOLD" -> "فروخته شده";
            case "DELETED" -> "حذف شده";
            default -> status;
        };
    }

    /**
     * Formats a BigDecimal price to a readable Persian string.
     *
     * @param price the price to format
     * @return formatted price string (e.g., "۱,۰۰۰,۰۰۰ تومان")
     */
    private String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }

    /**
     * Shows an error message and navigates back to the dashboard.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            AlertUtil.showError(message);
            goBack();
        });
    }
}