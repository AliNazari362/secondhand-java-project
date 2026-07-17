package controller;

import javafx.scene.layout.VBox;

public class AdminController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن پنل مدیریت
        }
        return root;
    }
}