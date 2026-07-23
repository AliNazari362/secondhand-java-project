package controller;

import config.DataReceiver;
import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.enums.AdvType;
import model.request.CommentRequest;
import model.response.*;
import service.AdvService;
import service.CommentService;
import service.FavoriteService;
import service.RatingService;
import utils.*;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for the advertisement detail page.
 * <p>
 * Displays full information about an advertisement including images, options,
 * comments, and allows actions like chat, favorites, rating, edit, delete, and mark as sold.
 * All FXML fields are null-checked to prevent NPE.
 * </p>
 *
 * @see AdvService
 * @see FavoriteService
 */
public class AdDetailController implements DataReceiver {

    // ==================== FXML Fields ====================
    @FXML
    private BorderPane rootPan;
    @FXML
    private Text titleText;
    @FXML
    private Label statusLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Text descriptionText;
    @FXML
    private Text priceText;
    @FXML
    private Text cityText;
    @FXML
    private Text addressText;
    @FXML
    private Text dateText;
    @FXML
    private Text ownerText;
    @FXML
    private Text categoryText;
    @FXML
    private Text ratingValueText;
    @FXML
    private Text ratingCountText;
    @FXML
    private FlowPane imagesContainer;
    @FXML
    private FlowPane optionsContainer;
    @FXML
    private VBox commentsContainer;
    @FXML
    private VBox productFields;
    @FXML
    private VBox serviceFields;
    @FXML
    private Text productBrandText;
    @FXML
    private Text productModelText;
    @FXML
    private Text productStateText;
    @FXML
    private Text productConstructorText;
    @FXML
    private Text serviceTypeText;
    @FXML
    private Text serviceCostText;

    @FXML
    private Button chatBtn;
    @FXML
    private Button favBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button soldBtn;
    @FXML
    private TextArea commentArea;

    // ==================== Fields ====================
    private UUID advId;
    private AdvertisementDetailDto currentAd;
    private boolean isFavorite = false;

    // ==================== DataReceiver Implementation ====================

    /**
     * Receives the advertisement UUID from the previous page and triggers data loading.
     *
     * @param data the advertisement UUID to load details for
     */
    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID uuid) {
            this.advId = uuid;
            loadAdDetail();
        } else AlertUtil.showError("شناسه آگهی نامعتبر است.");
    }

    // ==================== Loading Methods ====================

    /**
     * Loads the full advertisement details from the backend service.
     * Shows an error if the advertisement ID is null or invalid.
     */
    private void loadAdDetail() {
        if (advId == null) {
            AlertUtil.showError("شناسه آگهی معتبر نیست.");
            return;
        }

        try {
            currentAd = AdvService.getAdvDetail(advId.toString());
            Platform.runLater(this::displayAdDetail);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    // ==================== Display Methods ====================

    /**
     * Displays all loaded advertisement details in the UI.
     * Shows a 404 page if the advertisement is null.
     * All FXML fields are null-checked to avoid NullPointerException.
     */
    private void displayAdDetail() {
        if (currentAd == null) {
            Utils.show404Page(
                    rootPan, "آگهی مورد نظر یافت نشد",
                    "متاسفانه آگهی که به دنبال آن هستید وجود ندارد یا حذف شده است"
            );
            return;
        }

        try {
            getBasicInfo();
            UserSummaryDto owner = currentAd.getOwner();
            if (ownerText != null && owner != null) {
                ownerText.setText(owner.getFullName() + " (" + owner.getEmail() + ")");
            }

            loadRatingInfo();
            displayImages();
            displayOptions();
            displayTypeSpecificFields();
            updateActionButtons();
            checkFavoriteStatus();
            displayComments();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Populates basic advertisement information fields including title, status,
     * description, price, city, address, date, and category.
     */
    private void getBasicInfo() {
        if (typeLabel != null) {
            typeLabel.setText(currentAd.getAdvType() == AdvType.PRODUCT ? "کالا" : "خدمت");
        }

        if (titleText != null) {
            titleText.setText(currentAd.getFullName());
        }

        if (statusLabel != null) {
            statusLabel.setText(Utils.translateStatus(currentAd.getStatus().name()));
            statusLabel.getStyleClass().add("status-" + currentAd.getStatus().name().toLowerCase());
        }

        if (descriptionText != null) {
            descriptionText.setText(currentAd.getDescription() != null
                    ? currentAd.getDescription()
                    : "توضیحاتی ثبت نشده است.");
        }

        if (priceText != null) {
            if (currentAd.getProductDetail() != null) {
                priceText.setText(Utils.formatPrice(currentAd.getProductDetail().getPrice()));
                priceText.setVisible(true);
            } else if (currentAd.getServiceDetail() != null) {
                priceText.setText(Utils.formatPrice(currentAd.getServiceDetail().getCostOfPart()) +
                        " / " + currentAd.getServiceDetail().getTypeOfPart().name());
                priceText.setVisible(true);
            } else {
                priceText.setVisible(false);
            }
        }

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
                    ? currentAd.getCreationDate().format(Utils.FORMATTER)
                    : "");
        }

        if (categoryText != null) {
            categoryText.setText(currentAd.getCategoryName() != null
                    ? currentAd.getCategoryName()
                    : "بدون دسته بندی");
        }
    }


    /**
     * Displays advertisement images in a horizontal gallery.
     * Shows a placeholder label if no images are available.
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
            ImageView imageView = getImage(imageDto);
            imageView.setOnMouseClicked(e -> showFullImage(getImage(imageDto)));

            VBox imageBox = new VBox(5);
            imageBox.setAlignment(Pos.CENTER);
            imageBox.setPadding(new Insets(5));
            imageBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            imageBox.getChildren().add(imageView);
            imagesContainer.getChildren().add(imageBox);
        }
    }

    /**
     * Creates an ImageView for the given image response DTO.
     * Attempts to load from local file first, then falls back to server URL.
     *
     * @param imageDto the image data transfer object containing the image path
     * @return an ImageView configured with the loaded image
     */
    private static ImageView getImage(ImageResponseDto imageDto) {
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
                String serverUrl = Utils.BASE_IMAGE_URL + imagePath;
                imageView.setImage(new Image(serverUrl));
            }
        } catch (Exception e) {
            // Ignore individual image load errors
        }
        return imageView;
    }

    /**
     * Displays key-value options/attributes of the advertisement.
     * Shows a placeholder message if no options are available.
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
     * Opens a full-screen modal window to display the image at full resolution.
     * Supports closing with the Escape key.
     *
     * @param thumbnailView the ImageView containing the image to display in full screen
     */
    private void showFullImage(ImageView thumbnailView) {
        if (thumbnailView == null || thumbnailView.getImage() == null) {
            AlertUtil.showError("تصویر در دسترس نیست.");
            return;
        }

        Stage fullScreenStage = new Stage();
        fullScreenStage.initModality(Modality.APPLICATION_MODAL);
        fullScreenStage.setFullScreen(true);

        ImageView fullImageView = new ImageView();
        fullImageView.setPreserveRatio(true);
        fullImageView.setSmooth(true);
        fullImageView.setImage(thumbnailView.getImage());

        fullImageView.fitWidthProperty().bind(fullScreenStage.widthProperty());
        fullImageView.fitHeightProperty().bind(fullScreenStage.heightProperty().subtract(80));

        Button closeBtn = createCloseBtnForImageViewer(fullScreenStage);

        VBox fullScreenLayout = new VBox(20);
        fullScreenLayout.setAlignment(Pos.CENTER);
        fullScreenLayout.setStyle("-fx-background-color: black; -fx-padding: 20;");
        fullScreenLayout.getChildren().addAll(fullImageView, closeBtn);

        Scene scene = new Scene(fullScreenLayout);
        fullScreenStage.setScene(scene);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                fullScreenStage.close();
            }
        });

        fullScreenStage.show();
    }

    /**
     * Creates a styled close button for the full-screen image viewer.
     *
     * @param fullScreenStage the stage to close when the button is clicked
     * @return a Button configured with hover effects and close action
     */
    private static Button createCloseBtnForImageViewer(Stage fullScreenStage) {
        Button closeBtn = new Button("بستن");
        closeBtn.setStyle(
                "-fx-background-color: #e53e3e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> fullScreenStage.close());

        closeBtn.setOnMouseEntered(e ->
                closeBtn.setStyle(
                        "-fx-background-color: #c53030; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 16px; " +
                                "-fx-padding: 10 30; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand;"
                )
        );
        closeBtn.setOnMouseExited(e ->
                closeBtn.setStyle(
                        "-fx-background-color: #e53e3e; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 16px; " +
                                "-fx-padding: 10 30; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand;"
                )
        );
        return closeBtn;
    }

    /**
     * Shows product-specific or service-specific fields based on advertisement type.
     * Hides both sections first, then shows only the appropriate one.
     */
    private void displayTypeSpecificFields() {
        if (productFields != null) {
            productFields.setVisible(false);
            productFields.setManaged(false);
        }
        if (serviceFields != null) {
            serviceFields.setVisible(false);
            serviceFields.setManaged(false);
        }

        if (currentAd.getProductDetail() != null) {
            if (productFields != null) {
                productFields.setVisible(true);
                productFields.setManaged(true);
            }
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
            if (serviceFields != null) {
                serviceFields.setVisible(true);
                serviceFields.setManaged(true);
            }
            if (serviceTypeText != null) {
                serviceTypeText.setText(currentAd.getServiceDetail().getTypeOfPart() != null
                        ? currentAd.getServiceDetail().getTypeOfPart().getPersianName()
                        : "نامشخص");
            }
            if (serviceCostText != null) {
                serviceCostText.setText(Utils.formatPrice(currentAd.getServiceDetail().getCostOfPart()));
            }
        }
    }

    /**
     * Loads rating information (average rating and total count) from the server.
     * Falls back to default values on error.
     */
    private void loadRatingInfo() {
        try {
            double avg = RatingService.getAverageRating(advId.toString());
            long count = RatingService.getRatingCount(advId.toString());
            if (ratingValueText != null) {
                ratingValueText.setText(String.format("%.1f", avg));
            }
            if (ratingCountText != null) {
                ratingCountText.setText("(" + count + " نظر)");
            }
        } catch (Exception e) {
            if (ratingValueText != null) ratingValueText.setText("۰");
            if (ratingCountText != null) ratingCountText.setText("(۰ نظر)");
        }
    }

    /**
     * Updates action button visibility based on ownership and admin status.
     * Chat, Favorite, and Rating are visible only for non-owners.
     * Edit, Delete are visible for owner or admin.
     * Sold button is visible for owner only if status is ACTIVE.
     */
    private void updateActionButtons() {
        UUID currentUserId = SessionManager.getUserId();
        boolean isOwner = currentAd.getOwner() != null &&
                currentAd.getOwner().getId().equals(currentUserId);

        if (chatBtn != null) {
            chatBtn.setVisible(!isOwner);
            chatBtn.setManaged(!isOwner);
        }
        if (favBtn != null) {
            favBtn.setVisible(!isOwner);
            favBtn.setManaged(!isOwner);
        }
        if (editBtn != null) {
            editBtn.setVisible(isOwner);
            editBtn.setManaged(isOwner);
        }
        if (deleteBtn != null) {
            deleteBtn.setVisible(isOwner);
            deleteBtn.setManaged(isOwner);
        }
        if (soldBtn != null) {
            soldBtn.setVisible(isOwner && currentAd.getStatus().name().equals("ACTIVE"));
            soldBtn.setManaged(isOwner && currentAd.getStatus().name().equals("ACTIVE"));
        }
    }

    /**
     * Checks whether the current advertisement is in the user's favorites list
     * and updates the favorite button accordingly.
     */
    private void checkFavoriteStatus() {
        if (!SessionManager.isLoggedIn() || advId == null) {
            isFavorite = false;
            updateFavoriteButton();
            return;
        }
        try {
            List<AdvertisementSummaryDto> favorites = FavoriteService.getFavorites();
            isFavorite = favorites.stream()
                    .anyMatch(ad -> ad.getId().equals(advId));
        } catch (Exception e) {
            isFavorite = false;
        }
        updateFavoriteButton();
    }

    /**
     * Updates the favorite button text and style based on the current favorite status.
     */
    private void updateFavoriteButton() {
        if (favBtn == null) return;
        if (isFavorite) {
            favBtn.setText("حذف از علاقه مندی");
            favBtn.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold;");
        } else {
            favBtn.setText("افزودن به علاقه مندی");
            favBtn.setStyle("-fx-text-fill: #2d3748;");
        }
    }

    /**
     * Displays the list of comments for the current advertisement.
     * Shows a placeholder message if no comments exist.
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

        for (CommentResponseDto comment : currentAd.getComments()) {
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
                    ? comment.getDate().format(Utils.FORMATTER)
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
     * Opens the chat page with the current advertisement ID.
     * Shows an error if the advertisement ID is invalid.
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
     * Toggles the advertisement in the user's favorites list.
     * Adds the ad to favorites if not already favorited, removes it otherwise.
     * Requires the user to be logged in.
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
                FavoriteService.removeFavorite(advId.toString());
                isFavorite = false;
                AlertUtil.showSuccess("آگهی از علاقه مندی ها حذف شد.");
            } else {
                FavoriteService.addFavorite(advId.toString());
                isFavorite = true;
                AlertUtil.showSuccess("آگهی به علاقه مندی ها اضافه شد.");
            }
            updateFavoriteButton();
        } catch (Exception e) {
            if (e.getMessage().contains("پیش از این ثبت شده است")) {
                isFavorite = true;
                updateFavoriteButton();
            }
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Submits a new comment with rating for the current advertisement.
     * Validates that the user is logged in and has entered both comment text and rating.
     * Reloads rating info after successful submission.
     */
    @FXML
    public void onSubmitComment() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            return;
        }

        String text = commentArea.getText().trim();
        if (text.isEmpty()) {
            AlertUtil.showError("لطفاً متن نظر را وارد کنید.");
            return;
        }

        Integer rate = onRate();
        if (rate == null) {
            AlertUtil.showError("لطفاً امتیاز خود را وارد کنید.");
            return;
        }

        try {
            CommentRequest request = new CommentRequest(text, rate);
            CommentResponseDto comment = CommentService.createComment(advId.toString(), request);

            if (currentAd.getComments() == null) {
                currentAd.setComments(new java.util.ArrayList<>());
            }
            currentAd.getComments().addFirst(comment);
            displayComments();
            commentArea.clear();
            AlertUtil.showSuccess("نظر شما با موفقیت ثبت شد.");

            loadRatingInfo();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Opens a rating dialog allowing the user to rate the advertisement from 1 to 5.
     *
     * @return the selected rating value, or null if the dialog was canceled
     */
    private Integer onRate() {
        if (!SessionManager.isLoggedIn()) {
            AlertUtil.showWarning("لطفاً ابتدا وارد حساب خود شوید.");
            return null;
        }

        if (advId == null) {
            AlertUtil.showError("شناسه آگهی نامعتبر است.");
            return null;
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(5, 1, 2, 3, 4, 5);
        dialog.setTitle("امتیازدهی");
        dialog.setHeaderText("به این آگهی امتیاز دهید");
        dialog.setContentText("امتیاز (۱ تا ۵):");
        Optional<Integer> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Navigates to the edit advertisement page with the current advertisement ID.
     */
    @FXML
    public void onEdit() {
        if (advId != null) {
            SceneManager.showPage(Pages.EDIT_AD, null, advId);
        }
    }

    /**
     * Deletes the current advertisement after user confirmation.
     * Navigates back to the dashboard on successful deletion.
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
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Marks the current advertisement as sold after user confirmation.
     * Reloads the advertisement details to reflect the updated status.
     */
    @FXML
    public void onMarkAsSold() {
        boolean confirm = AlertUtil.showConfirmation(
                "تغییر وضعیت به فروخته شده",
                "آیا از تغییر وضعیت این آگهی به فروخته شده اطمینان دارید؟"
        );
        if (!confirm) return;

        try {
            AdvService.markAsSold(advId.toString());
            AlertUtil.showSuccess("وضعیت آگهی به فروخته شده تغییر کرد.");
            loadAdDetail();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Navigates back to the main dashboard page.
     */
    @FXML
    public void goBack() {
        SceneManager.showPage(Pages.DASHBOARD, null);
    }
}