package service;

import model.request.CommentRequest;
import model.response.CommentResponseDto;

/**
 * Service class for rating-related API operations.
 * Provides methods for submitting ratings and retrieving rating statistics for advertisements.
 */
public class RatingService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Submits a rating for a specific advertisement.
     *
     * @param advId   the advertisement ID
     * @param request the rating request containing rating value and optional comment
     * @return the created comment/rating response DTO
     * @throws Exception if the API request fails
     */
    public static CommentResponseDto rateAdvertisement(String advId, CommentRequest request) throws Exception {
        String response = api.post("/ratings/" + advId, request);
        return api.fromJson(response, CommentResponseDto.class);
    }

    /**
     * Retrieves the average rating for a specific advertisement.
     *
     * @param advId the advertisement ID
     * @return the average rating value
     * @throws Exception if the API request fails
     */
    public static double getAverageRating(String advId) throws Exception {
        String response = api.get("/ratings/" + advId + "/average");
        return Double.parseDouble(response);
    }

    /**
     * Retrieves the total number of ratings for a specific advertisement.
     *
     * @param advId the advertisement ID
     * @return the total rating count
     * @throws Exception if the API request fails
     */
    public static long getRatingCount(String advId) throws Exception {
        String response = api.get("/ratings/" + advId + "/count");
        return Long.parseLong(response);
    }
}