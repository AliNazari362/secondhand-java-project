package controller;

import config.DataReceiver;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import model.response.AdvertisementDetailDto;
import org.kordamp.ikonli.javafx.FontIcon;
import service.AdvService;
import utils.Pages;
import utils.SceneManager;

import java.util.UUID;

public class AdDetailController implements DataReceiver {

    @FXML
    private BorderPane rootPan;
    // ============ Header Section ============
    @FXML
    private Text titleText;
    @FXML
    private Label statusLabel;
    @FXML
    private Text viewsText;
    @FXML
    private Text ratingValueText;
    @FXML
    private Text ratingCountText;
    @FXML
    private Button submitRatingBtn;

    // ============ Product Detail Fields ============
    @FXML
    private VBox productDetailFields;
    @FXML
    private Text productPriceText;
    @FXML
    private Text productStateText;
    @FXML
    private Text productBrandText;
    @FXML
    private Text productModelText;
    @FXML
    private Text productCategoryText;
    @FXML
    private Text productManufacturerText;

    // ============ Service Detail Fields ============
    @FXML
    private VBox serviceDetailFields;
    @FXML
    private Text serviceCalcTypeText;
    @FXML
    private Text servicePriceText;

    // ============ General Info Fields ============
    @FXML
    private Text cityText;
    @FXML
    private Text dateText;
    @FXML
    private Text ownerText;
    @FXML
    private Text addressText;
    @FXML
    private Text descriptionText;

    // ============ Additional Options ============
    @FXML
    private FlowPane optionsFlowPane;

    // ============ Comments Section ============
    @FXML
    private TextArea commentArea;
    @FXML
    private VBox commentsContainer;

    // ============ Action Buttons ============
    @FXML
    private Button chatBtn;
    @FXML
    private Button favBtn;
    @FXML
    private Button rateBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button soldBtn;

    // ============ Star Rating Buttons ============
    @FXML
    private Button star1;
    @FXML
    private Button star2;
    @FXML
    private Button star3;
    @FXML
    private Button star4;
    @FXML
    private Button star5;

    private UUID adId;

    public void setAdId(UUID adId) {
        this.adId = adId;
    }

    @Override
    public void receiveData(Object data) {
        if (data instanceof UUID uuid) {
            adId = uuid;
        }
    }

    private void localAdDetail() {
        if (adId == null) {
            show404Page();
            return;
        }

        try {
            AdvertisementDetailDto adv = AdvService.getAdvDetail(adId.toString());
            titleText.setText(adv.getFullName());
        } catch (Exception e) {
            show404Page();
        }
    }

    private void show404Page() {
        // مخفی کردن همه المنت‌های صفحه
        hideAllContent();

        // ایجاد صفحه 404
        VBox notFoundBox = new VBox(20);
        notFoundBox.setAlignment(Pos.CENTER);
        notFoundBox.setPadding(new Insets(50));
        notFoundBox.setStyle("-fx-background-color: #f7fafc;");

        // آیکون 404
        FontIcon notFoundIcon = new FontIcon("fas-exclamation-triangle");
        notFoundIcon.setIconSize(80);
        notFoundIcon.setIconColor(Paint.valueOf("#fc8181"));

        // متن 404
        Text errorCode = new Text("404");
        errorCode.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-fill: #e53e3e;");

        // متن پیام
        Text message = new Text("آگهی مورد نظر یافت نشد!");
        message.setStyle("-fx-font-size: 24px; -fx-fill: #4a5568; -fx-font-weight: bold;");

        // متن توضیح
        Text description = new Text("متاسفانه آگهی که به دنبال آن هستید وجود ندارد یا حذف شده است.");
        description.setStyle("-fx-font-size: 16px; -fx-fill: #718096;");

        // دکمه بازگشت
        Button backButton = new Button("بازگشت به لیست آگهی‌ها");
        backButton.setStyle(
                "-fx-background-color: #3182ce; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 12 24; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> SceneManager.showPage(Pages.LIST_ADS, null));

        notFoundBox.getChildren().addAll(notFoundIcon, errorCode, message, description, backButton);

        // جایگزاری در center صفحه
        rootPan.setCenter(notFoundBox);
    }

    private void hideAllContent() {
        // مخفی کردن هدر (جز دکمه برگشت)
        titleText.setVisible(false);
        statusLabel.setVisible(false);
        viewsText.setVisible(false);

        // مخفی کردن بخش‌های محصول و خدمت
        productDetailFields.setVisible(false);
        productDetailFields.setManaged(false);
        serviceDetailFields.setVisible(false);
        serviceDetailFields.setManaged(false);

        // مخفی کردن دکمه‌های عملیات
        chatBtn.setVisible(false);
        favBtn.setVisible(false);
        rateBtn.setVisible(false);
        editBtn.setVisible(false);
        deleteBtn.setVisible(false);
        soldBtn.setVisible(false);

        // مخفی کردن بخش نظرات
        commentArea.setVisible(false);
        commentsContainer.setVisible(false);
    }

    @FXML
    public void initialize() {
        localAdDetail();
    }

    @FXML
    public void onChat() {
        System.out.println("Chat clicked!");
    }

    @FXML
    public void onAddFavorite() {
        System.out.println("Add favorite clicked!");
    }

    @FXML
    public void onRate() {
        System.out.println("Rate clicked!");
    }

    @FXML
    public void onEdit() {
        System.out.println("Edit clicked!");
    }

    @FXML
    public void onDelete() {
        System.out.println("Delete clicked!");
    }

    @FXML
    public void onMarkAsSold() {
        System.out.println("Mark as sold clicked!");
    }

    @FXML
    public void goBack() {
        System.out.println("Go back clicked!");
    }
}