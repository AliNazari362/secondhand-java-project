package controller;

import javafx.scene.layout.VBox;

import java.util.UUID;

public class EditAdController {
    private static VBox root;

    public static VBox getRoot(UUID adId) {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن صفحه ویرایش آگهی با آیدی: adId
        }
        return root;
    }
}