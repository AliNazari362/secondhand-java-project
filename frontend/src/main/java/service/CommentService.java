package service;

import model.request.CommentRequest;
import model.response.CommentResponseDto;

import java.util.List;

public class CommentService {

    private static final ApiClient api = ApiClient.getInstance();

    public static List<CommentResponseDto> getCommentsForAdv(String advId) throws Exception {
        String response = api.get("/comments/" + advId + "/comments");
        CommentResponseDto[] comments = api.fromJson(response, CommentResponseDto[].class);
        return List.of(comments);
    }

    public static CommentResponseDto createComment(String advId, CommentRequest request) throws Exception {
        String response = api.post("/comments/" + advId + "/add-comment", request);
        return api.fromJson(response, CommentResponseDto.class);
    }

    public static String deleteComment(Long commentId) throws Exception {
        return api.delete("/comments/" + commentId + "/delete");
    }
}
