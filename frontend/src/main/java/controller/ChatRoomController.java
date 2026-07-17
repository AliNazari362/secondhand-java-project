package controller;

import javafx.scene.layout.VBox;

import java.util.UUID;

public class ChatRoomController {
    private static VBox root;

    public static VBox getRoot(UUID chatroomId) {
        if (root == null) {
            root = new VBox();
            // TODO: کامل کردن صفحه چت‌روم با آیدی: chatroomId
        }
        return root;
    }
}