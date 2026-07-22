package utils;

import javafx.application.Platform;
import service.AuthService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM HH:mm").withLocale(Locale.forLanguageTag("fa-IR"));

    public static void logout() {
        boolean confirm = AlertUtil.showConfirmation("خروج از حساب", "آیا از خروج از حساب کاربری خود اطمینان دارید؟");
        if (!confirm) return;
        try {
            AuthService.logout();
            Platform.runLater(()-> AlertUtil.showSuccess("شما با موفقیت خارج شدید."));
            SceneManager.showPage(Pages.LOGIN, null);
        } catch (Exception e) {
            AlertUtil.showError("خطا در خروج: " + e.getMessage());
        }
    }

    public static String formatPrice(BigDecimal price) {
        if (price == null) return "۰ تومان";
        return String.format("%,d تومان", price.longValue());
    }

    public static String translateStatus(String status) {
        return switch (status) {
            case "ACTIVE" -> "فعال";
            case "PENDING" -> "در انتظار بررسی";
            case "REJECTED" -> "رد شده";
            case "SOLD" -> "فروخته شده";
            case "DELETED" -> "حذف شده";
            default -> status;
        };
    }
}
