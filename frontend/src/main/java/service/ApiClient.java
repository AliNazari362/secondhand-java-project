package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * کلاینت ارتباط با Backend.
 * تمام درخواست‌های HTTP (GET, POST, PUT, DELETE) از این کلاس عبور می‌کنند.
 */
public class ApiClient {

    // آدرس پایه سرور (بعداً توسط هم‌تیمی شما تنظیم می‌شود)
    private static final String BASE_URL = "http://localhost:8080/api";

    // HttpClient با timeout 10 ثانیه
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Gson با پشتیبانی از LocalDateTime
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                            context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    // ---------- متدهای عمومی ----------

    /**
     * ارسال درخواست GET به سرور.
     */
    public static String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthHeader())
                .GET()
                .build();
        return sendRequest(request);
    }

    /**
     * ارسال درخواست POST به سرور با بدنه‌ی JSON.
     */
    public static String post(String endpoint, Object body) throws Exception {
        String jsonBody = (body instanceof String) ? (String) body : gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", getAuthHeader())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }

    /**
     * ارسال درخواست PUT به سرور با بدنه‌ی JSON.
     */
    public static String put(String endpoint, Object body) throws Exception {
        String jsonBody = (body instanceof String) ? (String) body : gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", getAuthHeader())
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }

    /**
     * ارسال درخواست DELETE به سرور.
     */
    public static String delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthHeader())
                .DELETE()
                .build();
        return sendRequest(request);
    }

    // ---------- متد کمکی برای ارسال و دریافت پاسخ ----------

    private static String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // اگر خطای HTTP برگشت، استثنا پرتاب کن
        if (response.statusCode() >= 400) {
            throw new ApiException(
                    "خطای سرور: " + response.statusCode() + " - " + response.body(),
                    response.statusCode()
            );
        }

        return response.body();
    }

    // ---------- تبدیل JSON به آبجکت ----------

    /**
     * تبدیل رشته‌ی JSON به آبجکت مورد نظر.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // ---------- هدر احراز هویت ----------

    /**
     * اگر کاربر وارد شده باشد، هدر Authorization را با توکن JWT برمی‌گرداند.
     */
    private static String getAuthHeader() {
        if (SessionManager.isLoggedIn()) {
            return "Bearer " + SessionManager.getToken();
        }
        return "";
    }
}