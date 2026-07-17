package controller;

import javafx.scene.layout.VBox;

import java.util.UUID;

public class AdDetailController {
    private static VBox root;

    public static VBox getRoot(UUID adId) {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن صفحه جزئیات آگهی با آیدی: adId
        }
        return root;
    }
}