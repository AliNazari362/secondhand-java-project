package controller;

import javafx.scene.layout.VBox;

public class DashboardController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن داشبورد (لیست آگهی‌ها)
        }
        return root;
    }
}