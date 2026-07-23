package utils;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Category;
import model.request.OptionRequest;
import org.kordamp.ikonli.javafx.FontIcon;
import service.AuthService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * General utility class providing helper methods for common operations.
 * Includes formatting, image handling, logout flow, 404-page rendering,
 * and category tree reconstruction.
 */
public class Utils {

    public static final String BASE_IMAGE_URL = "http://localhost:8080/";
    public static final int MAX_IMAGES = 5;
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("d MMMM HH:mm")
            .withLocale(Locale.forLanguageTag("fa-IR"));

    /**
     * Logs out the current user after confirmation.
     * Clears the session and navigates to the login page.
     */
    public static void logout() {
        boolean confirm = AlertUtil.showConfirmation("خروج از حساب", "آیا از خروج از حساب کاربری خود اطمینان دارید؟");
        if (!confirm) return;
        try {
            AuthService.logout();
            AlertUtil.showSuccess("شما با موفقیت خارج شدید.");
            SceneManager.showPage(Pages.LOGIN, null);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Formats a BigDecimal price value as a human-readable string with a thousand separators.
     *
     * @param price the price value to format
     * @return the formatted price string in Persian numerals with "تومان" suffix
     */
    public static String formatPrice(BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }

    /**
     * Translates an advertisement status enum name to its Persian equivalent.
     *
     * @param status the status string from the enum
     * @return the Persian translation of the status
     */
    public static String translateStatus(String status) {
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
     * Extracts key-value option pairs from the dynamic options container.
     *
     * @param options           the list to populate with extracted OptionRequest objects
     * @param optionsContainer  the Vbox containing the dynamic option input rows
     */
    public static void getAllFeaturesOfAdv(List<OptionRequest> options, VBox optionsContainer) {
        options.clear();
        for (Node node : optionsContainer.getChildren()) {
            if (node instanceof HBox box) {
                if (box.getChildren().size() >= 3) {
                    TextField keyField = (TextField) box.getChildren().get(0);
                    TextField valueField = (TextField) box.getChildren().get(1);
                    String key = keyField.getText().trim();
                    String value = valueField.getText().trim();
                    if (!key.isEmpty() && !value.isEmpty()) {
                        options.add(new OptionRequest(key, value));
                    }
                }
            }
        }
    }

    /**
     * Opens a file chooser dialog for selecting advertisement images.
     * Validates file format and size, then adds previews to the container.
     *
     * @param selectedImageFiles     the list to add selected files to
     * @param imagePreviewContainer  the FlowPane to display image previews in
     */
    public static void chooseImage(List<File> selectedImageFiles, FlowPane imagePreviewContainer) {
        int currentCount = selectedImageFiles.size();
        if (currentCount >= MAX_IMAGES) {
            AlertUtil.showWarning("حداکثر " + MAX_IMAGES + " تصویر می توانید انتخاب کنید.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصاویر آگهی");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "تصاویر (JPG, PNG, GIF, BMP, WEBP)",
                "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp", "*.webp"
        ));

        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files == null || files.isEmpty()) return;

        int remainingSlots = MAX_IMAGES - currentCount;
        if (files.size() > remainingSlots) {
            AlertUtil.showWarning("حداکثر می توانید " + remainingSlots + " تصویر دیگر انتخاب کنید.");
            files = files.subList(0, remainingSlots);
        }

        for (File file : files) {
            try {
                if (imageValidation(file, selectedImageFiles)) continue;
                addImagePreview(file, selectedImageFiles, imagePreviewContainer);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    /**
     * Validates an image file's format and size before adding it to the selection.
     *
     * @param file               the image file to validate
     * @param selectedImageFiles the current list of selected files
     * @return true if validation failed and the file should be skipped, false otherwise
     * @throws IOException if the file cannot be read
     */
    private static boolean imageValidation(File file, List<File> selectedImageFiles) throws IOException {
        if (!ImageUploadUtil.isValidImageFile(file.getName())) {
            AlertUtil.showWarning("فرمت فایل '" + file.getName() + "' پشتیبانی نمی شود.");
            return true;
        }
        byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
        if (!ImageUploadUtil.isImageSizeValid(bytes, Utils.MAX_IMAGE_SIZE_MB)) {
            AlertUtil.showWarning("حجم فایل '" + file.getName() + "' بیش از " + Utils.MAX_IMAGE_SIZE_MB + " مگابایت است.");
            return true;
        }
        selectedImageFiles.add(file);
        return false;
    }

    /**
     * Adds an image preview thumbnail to the preview container with a remove button.
     *
     * @param file                  the selected image file
     * @param selectedImageFiles    the list tracking all selected files
     * @param imagePreviewContainer the container to add the preview to
     */
    private static void addImagePreview(File file, List<File> selectedImageFiles, FlowPane imagePreviewContainer) {
        try {
            Image image = new Image(file.toURI().toString(), 100, 100, true, true);

            VBox previewBox = new VBox(5);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setStyle("-fx-background-color: #f7fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 5;");

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);

            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: #fc8181; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6; -fx-cursor: hand; -fx-background-radius: 50%;");
            removeBtn.setOnAction(e -> {
                selectedImageFiles.remove(file);
                imagePreviewContainer.getChildren().remove(previewBox);
            });

            long fileSize = file.length();
            String sizeStr = (fileSize / 1024) + " KB";
            Text sizeText = new Text(sizeStr);
            sizeText.setStyle("-fx-font-size: 9px; -fx-fill: #718096;");

            previewBox.getChildren().addAll(imageView, sizeText, removeBtn);
            imagePreviewContainer.getChildren().add(previewBox);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Displays a styled 404 page in the given BorderPane.
     *
     * @param rootPan the BorderPane to render the 404-page in
     * @param text    the main error message text
     * @param desc    the description text below the main message
     */
    public static void show404Page(BorderPane rootPan, String text, String desc) {
        rootPan.getChildren().clear();

        VBox container404 = new VBox(30);
        container404.setAlignment(Pos.CENTER);
        container404.setStyle("-fx-background-color: #f0f4f8; -fx-padding: 50;");

        FontIcon icon404 = new FontIcon("fas-exclamation-triangle");
        icon404.setIconSize(80);
        icon404.setIconColor(Paint.valueOf("#e53e3e"));

        Text title404 = new Text("۴۰۴");
        title404.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Text message404 = new Text(text);
        message404.setStyle("-fx-font-size: 24px; -fx-fill: #4a5568;");

        Text desc404 = new Text(desc);
        desc404.setStyle("-fx-font-size: 14px; -fx-fill: #718096;");

        Button backBtn = createBackBtn();

        container404.getChildren().addAll(icon404, title404, message404, desc404, backBtn);

        rootPan.setCenter(container404);
    }

    /**
     * Creates a styled back button that navigates to the dashboard.
     *
     * @return a Button configured with hover effects and navigation action
     */
    private static Button createBackBtn() {
        Button backBtn = new Button("بازگشت به صفحه اصلی");
        backBtn.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10 25; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> SceneManager.showPage(Pages.DASHBOARD, null));

        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #2c5282; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 8;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 8;"));

        FontIcon backIcon = new FontIcon("fas-arrow-right");
        backIcon.setIconSize(16);
        backBtn.setGraphic(backIcon);
        return backBtn;
    }

    /**
     * Reconstructs parent-child relationships for a list of categories using their parentId fields.
     * Populates the subCategories list of each parent category.
     *
     * @param allCategories the flat list of all categories
     * @param categoryMap   a map of category IDs to Category objects for efficient lookup
     */
    public static void loadAllCategories(List<Category> allCategories, Map<Long, Category> categoryMap){
        for (Category cat : allCategories) {
            Long pid = cat.getParentId();
            if (pid != null) {
                Category parent = categoryMap.get(pid);
                if (parent != null) {
                    cat.setParent(parent);
                    if (!parent.getSubCategories().contains(cat)) {
                        parent.getSubCategories().add(cat);
                    }
                }
            }
        }
    }
}