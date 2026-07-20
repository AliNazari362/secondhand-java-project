package controller;

import exception.ExceptionHandler;
import model.request.ChatroomCreateRequest;
import model.response.AdvertisementDetailDto;
import model.response.ChatroomDetailDto;
import service.ApiClient;
import utils.AlertUtil;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.UUID;

public class AdDetailController {

    public static VBox getRoot(UUID adId) {
        return createRoot(adId);
    }

    private static VBox createRoot(UUID adId) {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createHeader(header, "📄 جزئیات آگهی");

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPrefWidth(700);

        try {
            String response = ApiClient.get("/advs/" + adId);
            AdvertisementDetailDto adv = ApiClient.fromJson(response, AdvertisementDetailDto.class);


            Text adTitle = new Text(adv.getFullName());
            adTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #1a202c;");

            // TODO specially for each status, page is different;
            Label statusBadge = new Label(adv.getStatus().getLabel());
            statusBadge.getStyleClass().addAll("status-badge", "status-active");

            // TODO configure price or cost
            VBox infoBox = new VBox(8);
            Label priceLabel = new Label("💰 قیمت: ۱۸,۰۰۰,۰۰۰ تومان");
            priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2d3748;");

            // TODO city must be Persian;
            Label cityLabel = new Label("شهر: " + adv.getCity().name().toLowerCase());
            Label ownerLabel = new Label("فروشنده: " + adv.getOwner().getFullName());
            Label dateLabel = new Label("تاریخ ثبت: " + adv.getCreationDate());

            Label descTitle = new Label("توضیحات:");
            descTitle.setStyle("-fx-font-weight: bold; -fx-fill: #2d3748;");

            Text descText = new Text(adv.getDescription());
            descText.setWrappingWidth(600);

            // TODO show address and options of adv
            // TODO show especially options of product or service

            HBox btnBox = new HBox(10);
            btnBox.setAlignment(Pos.CENTER);

            if (SessionManager.getUserId() == adv.getOwner().getId()) {
                Button editBtn = new Button("✏️ ویرایش");
                editBtn.getStyleClass().add("primary-btn");
                editBtn.setOnAction(e -> SceneManager.showEditAdPage(adId));

                Button deleteBtn = new Button("🗑️ حذف");
                deleteBtn.getStyleClass().add("danger-btn");
                deleteBtn.setOnAction(e -> {
                    try {
                        ApiClient.delete("/advs/" + adId + "/delete-adv");
                        AlertUtil.showSuccess("آگهی با موفقیت حذف شد!");
                    } catch (Exception ex) {
                        ExceptionHandler.handle(ex);
                    }
                });
                btnBox.getChildren().addAll(editBtn, deleteBtn);
            } else {
                Button chatBtn = new Button("💬 پیام به فروشنده");
                chatBtn.getStyleClass().add("primary-btn");
                chatBtn.setOnAction(e -> {
                    try {
                        ChatroomCreateRequest request = new ChatroomCreateRequest(adId);
                        String chatResponse = ApiClient.post("/chats/start-chat", request);
                        ChatroomDetailDto chatroomDetailDto = ApiClient.fromJson(chatResponse, ChatroomDetailDto.class);
                        SceneManager.showChatRoomPage(chatroomDetailDto.getId());
                    } catch (Exception ex) {
                        ExceptionHandler.handle(ex);
                    }
                });

                // TODO subscribe (is done) or UNSUBSCRIBED (must implement)
                Button favBtn = new Button("❤️ افزودن به علاقه‌مندی");
                favBtn.getStyleClass().add("secondary-btn");
                favBtn.setOnAction(e -> {
                    try {
                        ApiClient.post("favorites/add-favorite", adId);
                        AlertUtil.showSuccess("با موفقیت با علاقه مندی ها اضافه شد");
                    } catch (Exception ex) {
                        ExceptionHandler.handle(ex);
                    }
                });

                // TODO add rating and comment section
                Button rateBtn = new Button("ثبت نظر");
                rateBtn.getStyleClass().add("secondary-btn");
//            rateBtn.setOnAction();
                btnBox.getChildren().addAll(chatBtn, favBtn, rateBtn);
            }

            infoBox.getChildren().addAll(priceLabel, cityLabel, ownerLabel, dateLabel);
            card.getChildren().addAll(adTitle, statusBadge, infoBox, descTitle, descText, btnBox);
            content.getChildren().add(card);

        } catch (Exception ex) {
            ExceptionHandler.handle(ex);
        }


        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(AdDetailController.class.getResource("/style.css")).toExternalForm());
        return mainBox;
    }
}