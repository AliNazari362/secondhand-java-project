package controller;

import javafx.scene.layout.VBox;

public class RegisterController {
    private static VBox root;

    public static VBox getRoot() {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن صفحه ثبت‌نام
        }
        return root;
    }
}