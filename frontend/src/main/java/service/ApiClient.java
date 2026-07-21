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
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 *   ApiClient client = ApiClient.getInstance();
 *   String response = client.get("/users");
 *   User user = client.fromJson(response, User.class);
 * }</pre>
 *
 * @author [Your Name]
 * @version 1.0
 * @see SessionManager
 * @see ApiException
 */
public class ApiClient {

    /** Singleton instance of the ApiClient. */
    private static ApiClient INSTANCE;

    /**
     * Returns the singleton instance of the ApiClient.
     * <p>
     * Creates a new instance on first invocation using lazy initialization.
     * Subsequent calls return the same instance.
     * </p>
     *
     * @return the singleton {@code ApiClient} instance, never {@code null}
     */
    public static ApiClient getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ApiClient();
        return INSTANCE;
    }

    /**
     * The base URL of the backend server.
     * <p>
     * This value is expected to be configured by another team member to point
     * to the appropriate backend environment (development, staging, production).
     * </p>
     */
    private final String BASE_URL = "http://localhost:8080/api";

    /**
     * The HTTP client with a 10-second connection timeout.
     * <p>
     * Requests that fail to establish a connection within this duration
     * will throw a timeout exception.
     * </p>
     */
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Gson instance configured with custom {@link LocalDateTime} serialization/deserialization.
     * <p>
     * Uses {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} format for consistent
     * date-time representation across the API boundary.
     * </p>
     */
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                            context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    // ---------- Public HTTP Methods ----------

    /**
     * Sends an HTTP GET request to the specified API endpoint.
     * <p>
     * Includes the Authorization header if the user is currently logged in
     * (see {@link SessionManager#isLoggedIn()}).
     * </p>
     *
     * @param endpoint the API endpoint path relative to {@link #BASE_URL}
     *                 (e.g., {@code "/users"} or {@code "/tasks/123"})
     * @return the response body as a JSON string
     * @throws Exception          if a network error occurs or the request cannot be completed
     * @throws ApiException       if the server returns an HTTP error status code (4xx or 5xx)
     * @throws InterruptedException if the request is interrupted
     */
    public String get(String endpoint) throws Exception {
        String fullUrl = BASE_URL + endpoint;
        System.out.println("🟢 GET request to: " + fullUrl); // <-- اضافه کنید
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Authorization", getAuthHeader())
                .GET()
                .build();
        return sendRequest(request);
    }

    /**
     * Sends an HTTP POST request with a JSON body to the specified API endpoint.
     * <p>
     * If {@code body} is already a {@link String}, it is sent as-is; otherwise,
     * it is serialized to JSON using Gson. The Content-Type header is set to
     * {@code application/json}.
     * </p>
     *
     * @param endpoint the API endpoint path relative to {@link #BASE_URL}
     * @param body     the request body object to be serialized to JSON,
     *                 or a pre-serialized JSON string
     * @return the response body as a JSON string
     * @throws Exception          if a network error occurs or the request cannot be completed
     * @throws ApiException       if the server returns an HTTP error status code (4xx or 5xx)
     * @throws InterruptedException if the request is interrupted
     */
    public String post(String endpoint, Object body) throws Exception {
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
     * Sends an HTTP PUT request with a JSON body to the specified API endpoint.
     * <p>
     * If {@code body} is already a {@link String}, it is sent as-is; otherwise,
     * it is serialized to JSON using Gson. The Content-Type header is set to
     * {@code application/json}.
     * </p>
     *
     * @param endpoint the API endpoint path relative to {@link #BASE_URL}
     * @param body     the request body object to be serialized to JSON,
     *                 or a pre-serialized JSON string
     * @return the response body as a JSON string
     * @throws Exception          if a network error occurs or the request cannot be completed
     * @throws ApiException       if the server returns an HTTP error status code (4xx or 5xx)
     * @throws InterruptedException if the request is interrupted
     */
    public String put(String endpoint, Object body) throws Exception {
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
     * Sends an HTTP DELETE request to the specified API endpoint.
     * <p>
     * Includes the Authorization header if the user is currently logged in
     * (see {@link SessionManager#isLoggedIn()}).
     * </p>
     *
     * @param endpoint the API endpoint path relative to {@link #BASE_URL}
     *                 (e.g., {@code "/users/123"})
     * @return the response body as a JSON string
     * @throws Exception          if a network error occurs or the request cannot be completed
     * @throws ApiException       if the server returns an HTTP error status code (4xx or 5xx)
     * @throws InterruptedException if the request is interrupted
     */
    public String delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", getAuthHeader())
                .DELETE()
                .build();
        return sendRequest(request);
    }

    // ---------- Private Helper Method ----------

    /**
     * Sends an HTTP request and processes the response.
     * <p>
     * If the server responds with an HTTP status code of 400 or higher,
     * an {@link ApiException} is thrown containing the response body and
     * the status code for upstream error handling.
     * </p>
     *
     * @param request the fully constructed {@link HttpRequest} to send
     * @return the response body as a string, if the request was successful (status &lt; 400)
     * @throws Exception          if a network error occurs or the request cannot be completed
     * @throws ApiException       if the response status code is 400 or greater,
     *                            with the error message containing the response body
     * @throws InterruptedException if the request is interrupted during transmission
     */
    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If an HTTP error is returned, throw an exception
        if (response.statusCode() >= 400) {
            throw new ApiException(
                    response.body(), response.statusCode()
            );
        }

        return response.body();
    }

    // ---------- JSON Deserialization ----------

    /**
     * Deserializes a JSON string into an object of the specified type.
     * <p>
     * Uses the configured Gson instance with {@link LocalDateTime} support.
     * </p>
     *
     * @param <T>   the target type to deserialize into
     * @param json  the JSON string to parse, must not be {@code null}
     * @param clazz the class object representing the target type {@code T}
     * @return an instance of {@code T} populated from the JSON data
     * @throws com.google.gson.JsonSyntaxException if the JSON is not valid
     *         or does not match the expected structure
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // ---------- Authentication Header ----------

    /**
     * Constructs the Authorization header value for authenticated requests.
     * <p>
     * If a user is currently logged in (as determined by
     * {@link SessionManager#isLoggedIn()}), this method returns a Bearer token
     * string containing the JWT from {@link SessionManager#getToken()}.
     * Otherwise, an empty string is returned, resulting in no effective
     * authorization for the request.
     * </p>
     *
     * @return the Authorization header value in the format {@code "Bearer <token>"},
     *         or an empty string if no user is authenticated
     */
    private String getAuthHeader() {
        if (SessionManager.isLoggedIn()) {
            return "Bearer " + SessionManager.getToken();  // 👈 فقط اینجا "Bearer " را اضافه کنید
        }
        return "";
    }

    public String uploadFile(String path, byte[] imageBytes, String fileName) {
        // TODO complete this
        return "";
    }
}