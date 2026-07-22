package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ApiException;
import utils.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpRequest.BodyPublishers;

/**
 * Centralized HTTP client for communicating with the backend REST API.
 * <p>
 * This class implements the Singleton pattern and serves as the single point
 * of contact for all HTTP operations (GET, POST, PUT, DELETE) between the
 * client application and the backend server. It handles request construction,
 * authentication header injection, JSON serialization/deserialization, and
 * error response handling.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Singleton instance management via {@link #getInstance()}</li>
 *   <li>Automatic JWT token injection from {@link SessionManager}</li>
 *   <li>JSON serialization with {@code LocalDateTime} support</li>
 *   <li>10-second connection timeout</li>
 *   <li>Automatic {@link ApiException} throwing for HTTP error responses (4xx, 5xx)</li>
 * </ul>
 */
public class ApiClient {
    /** Singleton instance of the ApiClient. */
    private static ApiClient INSTANCE;

    /**
     * Returns the singleton instance of the ApiClient.
     *
     * @return the singleton {@code ApiClient} instance, never {@code null}
     */
    public static ApiClient getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ApiClient();
        return INSTANCE;
    }

    /** The base URL of the backend server. */
    private final String BASE_URL = "http://localhost:8080/api";

    /** The HTTP client with a 10-second connection timeout. */
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Gson instance with custom LocalDateTime serialization/deserialization. */
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                            context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    // ===== متدهای GET, POST, PUT, DELETE =====

    public String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthHeader())
                .GET()
                .build();
        return sendRequest(request);
    }

    public String post(String endpoint, Object body) throws Exception {
        String jsonBody = (body instanceof String) ? (String) body : gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", getAuthHeader())
                .POST(BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }

    public String put(String endpoint, Object body) throws Exception {
        String jsonBody = (body instanceof String) ? (String) body : gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", getAuthHeader())
                .PUT(BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }

    public String delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthHeader())
                .DELETE()
                .build();
        return sendRequest(request);
    }

    /**
     * Sends an HTTP request and processes the response.
     *
     * @param request the fully constructed {@link HttpRequest} to send
     * @return the response body as a string, if the request was successful (status &lt; 400)
     * @throws Exception          if a network error occurs or the request cannot be completed
     * @throws ApiException       if the response status code is 400 or greater
     * @throws InterruptedException if the request is interrupted during transmission
     */
    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new ApiException(response.body(), response.statusCode());
        }
        return response.body();
    }

    /**
     * Deserializes a JSON string into an object of the specified type.
     *
     * @param <T>   the target type to deserialize into
     * @param json  the JSON string to parse
     * @param clazz the class object representing the target type {@code T}
     * @return an instance of {@code T} populated from the JSON data
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * Constructs the Authorization header value for authenticated requests.
     *
     * @return the Authorization header value in the format {@code "Bearer <token>"},
     *         or an empty string if no user is authenticated
     */
    private String getAuthHeader() {
        if (SessionManager.isLoggedIn()) {
            return "Bearer " + SessionManager.getToken();
        }
        return "";
    }

    // ============================================================
    //  متد آپلود فایل (تصویر) با multipart/form-data
    // ============================================================

    /**
     * Uploads a file (image) to the server using multipart/form-data.
     * <p>
     * This method sends a file to the {@code /images/upload} endpoint. The file
     * is sent as a multipart/form-data request with the field name "file".
     * The server returns the stored file path as a JSON string.
     * </p>
     *
     * @param imageBytes the binary data of the image to upload
     * @param fileName   the name of the file including its extension
     * @return the server-side path where the uploaded image was stored
     * @throws Exception if the upload fails due to network issues or server errors
     */
    public String uploadFile(byte[] imageBytes, String fileName) throws Exception {
        String boundary = "----JavaFXUploadBoundary" + System.currentTimeMillis();

        // ساخت بدنه multipart/form-data
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: " + getContentType(fileName) + "\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";

        // ترکیب هدر، داده‌های باینری و فوتر
        byte[] headerBytes = header.getBytes();
        byte[] footerBytes = footer.getBytes();
        byte[] body = new byte[headerBytes.length + imageBytes.length + footerBytes.length];

        System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
        System.arraycopy(imageBytes, 0, body, headerBytes.length, imageBytes.length);
        System.arraycopy(footerBytes, 0, body, headerBytes.length + imageBytes.length, footerBytes.length);

        // ساخت درخواست
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/images/upload"))
                .header("Authorization", getAuthHeader())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(BodyPublishers.ofByteArray(body))
                .build();

        String response = sendRequest(request);
        // پاسخ شامل مسیر ذخیره‌شده است (با کوتیشن)
        return response.replace("\"", "").trim();
    }

    /**
     * Determines the MIME type of a file based on its extension.
     *
     * @param fileName the name of the file
     * @return the MIME type string
     */
    private String getContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }

    public String deleteWithBody(String endpoint, Object body) throws Exception {
        String jsonBody = (body instanceof String) ? (String) body : gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", getAuthHeader())
                .method("DELETE", BodyPublishers.ofString(jsonBody))
                .build();
        return sendRequest(request);
    }
}