package utils;

import exception.ExceptionHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

public class Utils {

    public static final String BASE_IMAGE_URL = "http://localhost:8080/";
    public static final int MAX_IMAGES = 5;
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("d MMMM HH:mm")
            .withLocale(Locale.forLanguageTag("fa-IR"));

    public static void logout() {
        boolean confirm = AlertUtil.showConfirmation("خروج از حساب", "آیا از خروج از حساب کاربری خود اطمینان دارید؟");
        if (!confirm) return;
        try {
            AuthService.logout();
            Platform.runLater(() -> AlertUtil.showSuccess("شما با موفقیت خارج شدید."));
            SceneManager.showPage(Pages.LOGIN, null);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    public static String formatPrice(BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }

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

    public static void chooseImage(List<File> selectedImageFiles, FlowPane imagePreviewContainer) {
        int currentCount = selectedImageFiles.size();
        if (currentCount >= MAX_IMAGES) {
            AlertUtil.showWarning("حداکثر " + MAX_IMAGES + " تصویر می‌توانید انتخاب کنید.");
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
            AlertUtil.showWarning("حداکثر می‌توانید " + remainingSlots + " تصویر دیگر انتخاب کنید.");
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

    private static boolean imageValidation(File file, List<File> selectedImageFiles) throws IOException {
        if (!ImageUploadUtil.isValidImageFile(file.getName())) {
            AlertUtil.showWarning("فرمت فایل '" + file.getName() + "' پشتیبانی نمی‌شود.");
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

        Text message404 = new Text();
        message404.setStyle("-fx-font-size: 24px; -fx-fill: #4a5568;");

        Text desc404 = new Text();
        desc404.setStyle("-fx-font-size: 14px; -fx-fill: #718096;");

        Button backBtn = createBackBtn();

        container404.getChildren().addAll(icon404, title404, message404, desc404, backBtn);

        rootPan.setCenter(container404);
    }

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
