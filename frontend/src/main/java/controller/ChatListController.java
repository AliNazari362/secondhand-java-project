package controller;

import javafx.scene.layout.VBox;

public class ChatListController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن لیست گفت‌وگوها
        }
        return root;
    }
}