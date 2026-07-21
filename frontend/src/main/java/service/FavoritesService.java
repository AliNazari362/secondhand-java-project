package service;

import model.response.AdvertisementSummaryDto;

import java.util.List;

public class FavoritesService {

    private final ApiClient api = ApiClient.getInstance();

    public List<AdvertisementSummaryDto> getFavorites() throws Exception {
        String response = api.get("/favorites");
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    public String addFavorite(String advId) throws Exception {
        return api.post("/favorites/add-favorite", advId);
    }

    public String removeFavorite(String advId) throws Exception {
        return api.delete("/favorites/delete-favorite" + advId);
    }

}
