package service;

import model.response.AdvertisementSummaryDto;
import java.util.List;

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
     *
     * @param advId the ID of the advertisement to add
     * @throws Exception if the API call fails
     */
    public void addFavorite(String advId) throws Exception {
        // ارسال advId به‌عنوان body (یا می‌توانید از query parameter استفاده کنید)
        api.post("/favorites/add-favorite", advId);
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