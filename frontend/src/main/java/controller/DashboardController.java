package controller;

import javafx.scene.control.ListCell;
import model.response.AdvertisementSummaryDto;
import service.SessionManager;
import utils.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class DashboardController {

    private static final ListView<AdvertisementSummaryDto> adListView = new ListView<>();

    public static VBox getRoot() {
        return createRoot();
    }

    private static VBox createRoot() {
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #f0f4f8;");

        // ---------- هدر ----------
        HBox header = new HBox(15);
        PublicElements.createBaseHeader(header);

        Text title = new Text("📋 لیست آگهی‌ها");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Text userInfo = new Text(SessionManager.getFullName() + " عزیز خوش آمدید");
        userInfo.setStyle("-fx-font-size: 14px; -fx-fill: #718096;");

        Button logoutBtn = new Button("🚪 خروج");
        logoutBtn.getStyleClass().add("secondary-btn");
        logoutBtn.setOnAction(e -> {
            SessionManager.clear();
            SceneManager.showLoginPage();
        });

        Button newAdBtn = new Button("➕ آگهی جدید");
        newAdBtn.getStyleClass().add("primary-btn");
        newAdBtn.setOnAction(e -> SceneManager.showNewAdPage());

        Button favBtn = new Button("❤️ علاقه‌مندی‌ها");
        favBtn.getStyleClass().add("secondary-btn");
        favBtn.setOnAction(e -> SceneManager.showFavoritesPage());

        Button chatBtn = new Button("💬 پیام‌ها");
        chatBtn.getStyleClass().add("secondary-btn");
        chatBtn.setOnAction(e -> SceneManager.showChatListPage());

        HBox rightBox = new HBox(10, newAdBtn, favBtn, chatBtn, logoutBtn);
        if (SessionManager.getRole().equals("ADMIN")) {
            Button adminBtn = new Button("⚙️ مدیریت");
            adminBtn.getStyleClass().add("danger-btn");
            adminBtn.setVisible(SessionManager.isAdmin());
            adminBtn.setOnAction(e -> SceneManager.showAdminPage());
            rightBox.getChildren().add(adminBtn);
        }

        rightBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, userInfo, rightBox);

        // ---------- محتوا ----------
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setAlignment(Pos.TOP_CENTER);

        VBox listCard = new VBox(10);
        listCard.getStyleClass().add("card");
        listCard.setPrefWidth(800);

        Label listTitle = new Label("آگهی‌های فعال");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        adListView.getStyleClass().add("list-view");
        adListView.setPrefHeight(400);
        adListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AdvertisementSummaryDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellBox = new VBox(3);
                    Label titleLabel = new Label(item.getFullName());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label infoLabel = new Label(
                            item.getCity() + " - " + item.getOwnerFullName() + " - " + item.getStatus()
                    );
                    infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

                    cellBox.getChildren().addAll(titleLabel, infoLabel);
                    setGraphic(cellBox);
                }
            }
        });


        adListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                AdvertisementSummaryDto adv = adListView.getSelectionModel().getSelectedItem();
                if (adv != null) SceneManager.showAdDetailPage(adv.getId());
            }
        });

        listCard.getChildren().addAll(listTitle, adListView);
        content.getChildren().add(listCard);

        mainBox.getChildren().addAll(header, content);
        mainBox.getStylesheets().add(Objects.requireNonNull(DashboardController.class.getResource("/style.css")).toExternalForm());

        return mainBox;
    }
}