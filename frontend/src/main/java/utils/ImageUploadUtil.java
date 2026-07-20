package utils;

import service.ApiClient;
import exception.ApiException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for uploading images to the backend server.
 * <p>
 * Provides convenience methods for uploading images from various sources:
 * raw byte arrays, local files, and input streams. All upload operations
 * delegate to {@link ApiClient} which handles the multipart/form-data
 * construction and HTTP communication.
 * </p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 *   // Upload from file
 *   String serverPath = ImageUploadUtil.uploadImageFromFile(Path.of("photo.jpg"));
 *
 *   // Upload from byte array
 *   byte[] imageData = ...;
 *   String serverPath = ImageUploadUtil.uploadImage(imageData, "avatar.png");
 *
 *   // Upload from InputStream
 *   InputStream stream = file.getInputStream();
 *   String serverPath = ImageUploadUtil.uploadImageFromStream(stream, "image.jpg");
 * }</pre>
 *
 * @author [Your Name]
 * @version 1.0
 * @see ApiClient
 */
public class ImageUploadUtil {

    /**
     * Uploads an image to the server using raw binary data.
     * <p>
     * Sends the image bytes to the {@code /images/upload} endpoint via
     * {@link ApiClient#uploadFile} using multipart/form-data encoding.
     * </p>
     *
     * @param imageBytes the binary data of the image to upload, must not be {@code null} or empty
     * @param fileName   the name of the file including its extension
     *                   (e.g., {@code "photo.jpg"}, {@code "avatar.png"})
     * @return the server-side path where the uploaded image was stored
     * @throws Exception          if the upload fails due to network issues, server errors,
     *                            or invalid input parameters
     * @throws ApiException       if the server returns an HTTP error response (4xx or 5xx)
     * @throws NullPointerException if {@code imageBytes} or {@code fileName} is {@code null}
     */
    public static String uploadImage(byte[] imageBytes, String fileName) throws Exception {
        return ApiClient.getInstance().uploadFile("/images/upload", imageBytes, fileName);
    }

    /**
     * Uploads an image from a local file path.
     * <p>
     * Reads all bytes from the specified file and delegates to
     * {@link #uploadImage(byte[], String)} for the actual upload.
     * The file name is extracted from the path using
     * {@link Path#getFileName()}.
     * </p>
     *
     * @param filePath the path to the local image file, must exist and be readable
     * @return the server-side path where the uploaded image was stored
     * @throws Exception             if the upload fails, the file does not exist,
     *                               or cannot be read
     * @throws java.io.IOException   if an I/O error occurs reading the file
     * @throws ApiException          if the server returns an HTTP error response (4xx or 5xx)
     * @throws NullPointerException  if {@code filePath} is {@code null}
     */
    public static String uploadImageFromFile(Path filePath) throws Exception {
        byte[] bytes = Files.readAllBytes(filePath);
        String fileName = filePath.getFileName().toString();
        return uploadImage(bytes, fileName);
    }

    /**
     * Uploads an image from an {@link InputStream}.
     * <p>
     * Reads all bytes from the input stream into memory using an 8KB buffer,
     * then delegates to {@link #uploadImage(byte[], String)} for the actual upload.
     * The input stream is fully consumed but <b>not closed</b> by this method;
     * the caller is responsible for closing the stream.
     * </p>
     *
     * <p><b>Note:</b> This method loads the entire image into memory. For very large
     * files, consider using a streaming approach if supported by the API.</p>
     *
     * @param inputStream the input stream containing the image data, must not be {@code null}
     *                    and must be open and readable
     * @param fileName    the name to assign to the uploaded file including extension
     *                    (e.g., {@code "screenshot.png"})
     * @return the server-side path where the uploaded image was stored
     * @throws Exception             if the upload fails or the stream cannot be read
     * @throws java.io.IOException   if an I/O error occurs reading from the stream
     * @throws ApiException          if the server returns an HTTP error response (4xx or 5xx)
     * @throws NullPointerException  if {@code inputStream} or {@code fileName} is {@code null}
     * @throws OutOfMemoryError      if the image data exceeds available heap memory
     */
    public static String uploadImageFromStream(InputStream inputStream, String fileName) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return uploadImage(buffer.toByteArray(), fileName);
    }
}