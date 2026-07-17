package controller;

import javafx.scene.layout.VBox;

public class NewAdController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن صفحه ثبت آگهی جدید
        }
        return root;
    }
}