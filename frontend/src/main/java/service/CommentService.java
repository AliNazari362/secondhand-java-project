package service;

import model.request.CommentRequest;
import model.response.CommentResponseDto;

import java.util.List;

/**
 * Service class for comment-related API operations.
 * Provides methods for creating, retrieving, and deleting comments on advertisements.
 */
public class CommentService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves all comments for a specific advertisement.
     *
     * @param advId the advertisement ID
     * @return a list of comments for the advertisement
     * @throws Exception if the API request fails
     */
    public static List<CommentResponseDto> getCommentsForAdv(String advId) throws Exception {
        String response = api.get("/comments/" + advId + "/comments");
        CommentResponseDto[] comments = api.fromJson(response, CommentResponseDto[].class);
        return List.of(comments);
    }

    /**
     * Creates a new comment on an advertisement.
     *
     * @param advId   the advertisement ID
     * @param request the comment request containing text and rating
     * @return the created comment response DTO
     * @throws Exception if the API request fails
     */
    public static CommentResponseDto createComment(String advId, CommentRequest request) throws Exception {
        String response = api.post("/comments/" + advId + "/add-comment", request);
        return api.fromJson(response, CommentResponseDto.class);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param commentId the comment ID to delete
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String deleteComment(Long commentId) throws Exception {
        return api.delete("/comments/" + commentId + "/delete");
    }
}