package controller;

import javafx.scene.layout.VBox;

public class FavoritesController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن لیست علاقه‌مندی‌ها
        }
        return root;
    }
}