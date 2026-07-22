package service;

import model.response.AdvertisementSummaryDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing user favorites.
 * Provides methods for adding, removing, and retrieving favorite advertisements.
 */
public class FavoriteService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves all favorite advertisements of the current user.
     *
     * @return list of favorite advertisement summaries
     * @throws Exception if the API call fails
     */
    public static List<AdvertisementSummaryDto> getFavorites() throws Exception {
        String response = api.get("/favorites");
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    /**
     * Adds an advertisement to the user's favorites.
     * Sends a JSON object with key "advId".
     *
     * @param advId the ID of the advertisement to add
     * @throws Exception if the API call fails
     */
    public static void addFavorite(String advId) throws Exception {
        String jsonBody = "\"" + advId + "\"";
        api.post("/favorites/add-favorite", jsonBody);
    }

    public static void removeFavorite(String advId) throws Exception {
        String jsonBody = "\"" + advId + "\"";
        api.deleteWithBody("/favorites/delete-favorite", jsonBody);
    }
//    /**
//     * Checks if an advertisement is in the user's favorites.
//     * If the API endpoint is not available (e.g., returns 404/500), it returns false.
//     *
//     * @param advId the ID of the advertisement
//     * @return true if the advertisement is in favorites, false otherwise
//     */
//    public boolean isFavorite(String advId) {
//        try {
//            String response = api.get("/favorites/is-favorite/" + advId);
//            return Boolean.parseBoolean(response);
//        } catch (Exception e) {
//            System.err.println("⚠️ isFavorite API failed, returning false: " + e.getMessage());
//            return false;
//        }
//    }
}