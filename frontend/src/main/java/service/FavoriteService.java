package service;

import model.response.AdvertisementSummaryDto;

import java.util.List;

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

    /**
     * Removes an advertisement from the user's favorites.
     * Sends a JSON object with key "advId" using DELETE with body.
     *
     * @param advId the ID of the advertisement to remove
     * @throws Exception if the API call fails
     */
    public static void removeFavorite(String advId) throws Exception {
        String jsonBody = "\"" + advId + "\"";
        api.deleteWithBody("/favorites/delete-favorite", jsonBody);
    }
}