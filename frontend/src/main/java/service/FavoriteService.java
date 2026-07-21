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

    private final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves all favorite advertisements of the current user.
     *
     * @return list of favorite advertisement summaries
     * @throws Exception if the API call fails
     */
    public List<AdvertisementSummaryDto> getFavorites() throws Exception {
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
    public void addFavorite(String advId) throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("advId", advId);
        api.post("/favorites/add-favorite", payload);
    }

    /**
     * Removes an advertisement from the user's favorites.
     *
     * @param advId the ID of the advertisement to remove
     * @throws Exception if the API call fails
     */
    public void removeFavorite(String advId) throws Exception {
        api.delete("/favorites/remove-favorite/" + advId);
    }

    /**
     * Checks if an advertisement is in the user's favorites.
     *
     * @param advId the ID of the advertisement
     * @return true if the advertisement is in favorites, false otherwise
     * @throws Exception if the API call fails
     */
    public boolean isFavorite(String advId) throws Exception {
        String response = api.get("/favorites/is-favorite/" + advId);
        return Boolean.parseBoolean(response);
    }
}