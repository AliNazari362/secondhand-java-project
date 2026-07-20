//package service;
//
//import model.response.AdvertisementSummaryDto;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//public class FavoriteService {
//
//    public List<AdvertisementSummaryDto> getFavorites() throws Exception {
//        String json = ApiClient.get("/favorites");
//        return Arrays.asList(ApiClient.fromJson(json, AdvertisementSummaryDto[].class));
//    }
//
//    public void addFavorite(UUID advId) throws Exception {
//        ApiClient.post("/favorites/add-favorite", advId);
//    }
//
//    public void removeFavorite(UUID advId) throws Exception {
//        ApiClient.delete("/favorites/delete-favorite");
//    }
//}