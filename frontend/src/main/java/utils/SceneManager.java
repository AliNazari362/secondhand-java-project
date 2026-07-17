package utils;

import controller.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.UUID;

public class SceneManager {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    // ---------- صفحات اصلی ----------
    public static void showLoginPage() {
        Scene scene = new Scene(LoginController.getRoot(), 500, 400);
        primaryStage.setTitle("ورود به سامانه");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showRegisterPage() {
        Scene scene = new Scene(RegisterController.getRoot(), 500, 500);
        primaryStage.setTitle("ثبت‌نام");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showDashboardPage() {
        Scene scene = new Scene(DashboardController.getRoot(), 800, 600);
        primaryStage.setTitle("لیست آگهی‌ها");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showNewAdPage() {
        Scene scene = new Scene(NewAdController.getRoot(), 700, 600);
        primaryStage.setTitle("ثبت آگهی جدید");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAdDetailPage(UUID adId) {
        Scene scene = new Scene(AdDetailController.getRoot(adId), 700, 600);
        primaryStage.setTitle("جزئیات آگهی");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showChatListPage() {
        Scene scene = new Scene(ChatListController.getRoot(), 700, 500);
        primaryStage.setTitle("گفت‌وگوها");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showChatRoomPage(UUID chatroomId) {
        Scene scene = new Scene(ChatRoomController.getRoot(chatroomId), 600, 500);
        primaryStage.setTitle("گفت‌وگو");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showFavoritesPage() {
        Scene scene = new Scene(FavoritesController.getRoot(), 800, 600);
        primaryStage.setTitle("علاقه‌مندی‌ها");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showEditAdPage(UUID adId) {
        Scene scene = new Scene(EditAdController.getRoot(adId), 700, 600);
        primaryStage.setTitle("ویرایش آگهی");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAdminPage() {
        Scene scene = new Scene(AdminController.getRoot(), 800, 600);
        primaryStage.setTitle("پنل مدیریت");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}