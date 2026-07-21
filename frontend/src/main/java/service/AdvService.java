package service;

import model.request.ProductCreateRequest;
import model.request.ProductUpdateRequest;
import model.request.ServiceCreateRequest;
import model.request.ServiceUpdateRequest;
import model.response.AdvertisementDetailDto;
import model.response.AdvertisementSummaryDto;

import java.util.List;

public class AdvService {

    private static final ApiClient api = ApiClient.getInstance();

    public static List<AdvertisementSummaryDto> getActiveAds(String keyword, String city) throws Exception {
        StringBuilder url = new StringBuilder("/advs/search");
        boolean isKeywordExist = keyword != null && !keyword.isBlank();

        if (isKeywordExist) url.append("?").append(keyword);
        if (city != null && !city.isBlank()) {
            if (isKeywordExist) url.append("&").append(city);
            else url.append("?").append(city);
        }

        String response = api.get(url.toString());
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    public static AdvertisementDetailDto getAdvDetail(String advId) throws Exception {
        String response = api.get("/advs/" + advId);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static List<AdvertisementSummaryDto> getUserAds(String userId) throws Exception {
        String response = api.get("/advs/user" + userId);
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    public static AdvertisementDetailDto createProduct(ProductCreateRequest request) throws Exception {
        String response = api.post("/advs/create-product", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static AdvertisementDetailDto createService(ServiceCreateRequest request) throws Exception {
        String response = api.post("/advs/create-service", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static AdvertisementDetailDto updateProduct(ProductUpdateRequest request) throws Exception {
        String response = api.put("/advs/update-product", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static AdvertisementDetailDto updateService(ServiceUpdateRequest request) throws Exception {
        String response = api.put("/advs/update-service", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static String markAsSold(String advId) throws Exception {
        return api.put("/advs/" + advId + "/mark-as-sold", null);
    }

    public static String deleteAdv(String advId) throws Exception {
        return api.delete("/advs/" + advId + "/delete-adv");
    }
}
