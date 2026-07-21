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
import service.FavoritesService;
import utils.AlertUtil;
import utils.Pages;
import utils.SceneManager;
import utils.SessionManager;

import java.io.File;
import java.util.UUID;

/**
 * Controller for the advertisement detail page.
 * Displays full information about an advertisement including images, options,
 * comments, and allows actions like chat, favorites, rating, edit, delete, and mark as sold.
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
    private final FavoritesService favoritesService = new FavoritesService();

    /**
     * Receives data passed from the previous page (the advertisement ID).
     *
     * @param data the advertisement UUID
     */
    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID uuid) {
            this.advId = uuid;
            loadAdDetail();
        }
    }

    /**
     * Loads the full advertisement details from the backend.
     */
    private void loadAdDetail() {
        if (advId == null) {
            showError("شناسه آگهی معتبر نیست.");
            return;
        }

        try {
            currentAd = AdvService.getAdvDetail(advId.toString());
            Platform.runLater(this::displayAdDetail);
        } catch (Exception e) {
            showError("خطا در دریافت اطلاعات آگهی: " + e.getMessage());
        }
    }

    /**
     * Displays the loaded advertisement details in the UI.
     */
    private void displayAdDetail() {
        if (currentAd == null) return;
        System.out.println("🔥🔥🔥 AdDetailController NEW VERSION LOADED! 🔥🔥🔥");

        // اطلاعات پایه
        titleText.setText(currentAd.getFullName());
        statusLabel.setText(translateStatus(currentAd.getStatus().name()));
        statusLabel.getStyleClass().add("status-" + currentAd.getStatus().name().toLowerCase());

        descriptionText.setText(currentAd.getDescription() != null ? currentAd.getDescription() : "توضیحاتی ثبت نشده است.");

        // قیمت (از ProductDetail یا ServiceDetail)
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

        cityText.setText(currentAd.getCity() != null ? currentAd.getCity().getPersianName() : "نامشخص");
        addressText.setText(currentAd.getAddress() != null ? currentAd.getAddress() : "آدرسی ثبت نشده است.");
        dateText.setText(currentAd.getCreationDate() != null ?
                currentAd.getCreationDate().toLocalDate().toString() : "");
        categoryText.setText(currentAd.getCategoryName() != null ? currentAd.getCategoryName() : "بدون دسته‌بندی");

        // مالک
        UserSummaryDto owner = currentAd.getOwner();
        if (owner != null) {
            ownerText.setText(owner.getFullName() + " (" + owner.getEmail() + ")");
        }

        // نمایش تصاویر
        displayImages();

        // نمایش ویژگی‌ها (Options)
        displayOptions();

        // نمایش فیلدهای اختصاصی (Product / Service)
        displayTypeSpecificFields();

        // نمایش دکمه‌های عملیاتی بر اساس مالکیت
        updateActionButtons();

        // بررسی وضعیت علاقه‌مندی (اختیاری)
        checkFavoriteStatus();

        // نمایش نظرات (در صورت وجود)
        displayComments();
    }

    /**
     * Displays images in a horizontal gallery.
     */
    private void displayImages() {
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
                    // Try loading from server
                    String serverUrl = "http://localhost:8080/" + imagePath;
                    imageView.setImage(new Image(serverUrl));
                }
            } catch (Exception e) {
                // Ignore
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
     */
    private void displayOptions() {
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
     */
    private void displayTypeSpecificFields() {
        productFields.setVisible(false);
        serviceFields.setVisible(false);

        if (currentAd.getProductDetail() != null) {
            productFields.setVisible(true);
            productBrandText.setText(currentAd.getProductDetail().getBrand() != null ?
                    currentAd.getProductDetail().getBrand() : "نامشخص");
            productModelText.setText(currentAd.getProductDetail().getModel() != null ?
                    currentAd.getProductDetail().getModel() : "نامشخص");
            productStateText.setText(currentAd.getProductDetail().getStateOfProduct() != null ?
                    currentAd.getProductDetail().getStateOfProduct().getPersianName() : "نامشخص");
            productConstructorText.setText(currentAd.getProductDetail().getConstructor() != null ?
                    currentAd.getProductDetail().getConstructor() : "نامشخص");
        } else if (currentAd.getServiceDetail() != null) {
            serviceFields.setVisible(true);
            serviceTypeText.setText(currentAd.getServiceDetail().getTypeOfPart() != null ?
                    currentAd.getServiceDetail().getTypeOfPart().name() : "نامشخص");
            serviceCostText.setText(formatPrice(currentAd.getServiceDetail().getCostOfPart()));
        }
    }

    /**
     * Updates action buttons based on whether the current user is the owner.
     */
    private void updateActionButtons() {
        UUID currentUserId = SessionManager.getUserId();
        boolean isOwner = currentAd.getOwner() != null &&
                currentAd.getOwner().getId().equals(currentUserId);

        chatBtn.setVisible(!isOwner);
        chatBtn.setManaged(!isOwner);

        favBtn.setVisible(!isOwner);
        favBtn.setManaged(!isOwner);

        rateBtn.setVisible(!isOwner);
        rateBtn.setManaged(!isOwner);

        editBtn.setVisible(isOwner);
        editBtn.setManaged(isOwner);

        deleteBtn.setVisible(isOwner);
        deleteBtn.setManaged(isOwner);

        soldBtn.setVisible(isOwner && currentAd.getStatus().name().equals("ACTIVE"));
        soldBtn.setManaged(isOwner && currentAd.getStatus().name().equals("ACTIVE"));
    }

    /**
     * Checks if the current advertisement is in the user's favorites (placeholder).
     */
    private void checkFavoriteStatus() {
        // فعلاً مقداردهی اولیه می‌شود – در آینده با یک API کامل می‌شود
        isFavorite = false;
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            favBtn.setText("❤️ حذف از علاقه‌مندی");
        } else {
            favBtn.setText("🤍 افزودن به علاقه‌مندی");
        }
    }

    /**
     * Displays comments for the advertisement.
     */
    private void displayComments() {
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

            Text authorText = new Text(comment.getAuthor() != null ? comment.getAuthor().getFullName() : "ناشناس");
            authorText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: #2d3748;");

            Text ratingText = new Text("⭐ " + comment.getRate() + "/5");
            ratingText.setStyle("-fx-font-size: 12px; -fx-fill: #f6ad55;");

            Text dateText = new Text(comment.getDate() != null ?
                    comment.getDate().toLocalDate().toString() : "");
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

    @FXML
    public void onChat() {
        AlertUtil.showWarning("قابلیت چت در حال توسعه است.");
    }

    @FXML
    public void onAddFavorite() {
        try {
            if (isFavorite) {
                favoritesService.removeFavorite(advId.toString());
                isFavorite = false;
                AlertUtil.showSuccess("آگهی از علاقه‌مندی‌ها حذف شد.");
            } else {
                favoritesService.addFavorite(advId.toString());
                isFavorite = true;
                AlertUtil.showSuccess("آگهی به علاقه‌مندی‌ها اضافه شد.");
            }
            updateFavoriteButton();
        } catch (Exception e) {
            AlertUtil.showError("خطا در عملیات علاقه‌مندی: " + e.getMessage());
        }
    }

    @FXML
    public void onRate() {
        AlertUtil.showWarning("قابلیت امتیازدهی در حال توسعه است.");
    }

    @FXML
    public void onEdit() {
        if (advId != null) {
            SceneManager.showPage(Pages.EDIT_AD, null, advId); // اضافه کردن null به عنوان عنوان
        }
    }

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
            loadAdDetail(); // بارگذاری مجدد
        } catch (Exception e) {
            AlertUtil.showError("خطا در تغییر وضعیت: " + e.getMessage());
        }
    }

    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }

    // ==================== Utility Methods ====================

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

    private String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            AlertUtil.showError(message);
            goBack();
        });
    }
}