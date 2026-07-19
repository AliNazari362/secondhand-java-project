package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import utils.SceneManager;


public class PublicElements {

    public static void createBaseHeader(HBox header) {
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");
    }

    public static void createHeader(HBox header, String text) {
        createBaseHeader(header);
        Text title = new Text(text);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #2d3748;");

        Button backBtn = new Button("🔙 بازگشت");
        backBtn.getStyleClass().add("secondary-btn");
        backBtn.setOnAction(e -> SceneManager.showDashboardPage());

        HBox rightBox = new HBox(backBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, rightBox);
    }

    public static Button createCancelBtn() {
        Button cancelBtn = new Button("❌ انصراف");
        cancelBtn.getStyleClass().add("secondary-btn");
        cancelBtn.setOnAction(e -> SceneManager.showDashboardPage());
        return cancelBtn;
    }
}
