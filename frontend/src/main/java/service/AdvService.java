package service;

import model.enums.City;
import model.request.ProductCreateRequest;
import model.request.ProductUpdateRequest;
import model.request.ServiceCreateRequest;
import model.request.ServiceUpdateRequest;
import model.response.AdvertisementDetailDto;
import model.response.AdvertisementSummaryDto;

import java.math.BigDecimal;
import java.util.List;

public class AdvService {

    private static final ApiClient api = ApiClient.getInstance();

    // ==================== SEARCH ====================

    public static List<AdvertisementSummaryDto> getActiveAds(
            String keyword,
            City city,
            Long categoryId,
            String sortBy,
            BigDecimal minPrice,
            BigDecimal maxPrice) throws Exception {

        StringBuilder url = new StringBuilder("/advs/search?");
        boolean hasParam = false;

        if (keyword != null && !keyword.isEmpty()) {
            url.append("keyword=").append(keyword);
            hasParam = true;
        }
        if (city != null) {
            if (hasParam) url.append("&");
            url.append("city=").append(city.name());
            hasParam = true;
        }
        if (categoryId != null) {
            if (hasParam) url.append("&");
            url.append("categoryId=").append(categoryId);
            hasParam = true;
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            if (hasParam) url.append("&");
            url.append("sortBy=").append(sortBy);
            hasParam = true;
        }
        if (minPrice != null) {
            if (hasParam) url.append("&");
            url.append("minPrice=").append(minPrice);
            hasParam = true;
        }
        if (maxPrice != null) {
            if (hasParam) url.append("&");
            url.append("maxPrice=").append(maxPrice);
            hasParam = true;
        }

        String finalUrl = hasParam ? url.toString() : "/advs/search";
        String response = api.get(finalUrl);
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    // ==================== DETAIL ====================

    public static AdvertisementDetailDto getAdvDetail(String advId) throws Exception {
        String response = api.get("/advs/" + advId);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static List<AdvertisementSummaryDto> getUserAds(String userId) throws Exception {
        String response = api.get("/advs/user/" + userId);
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    // ==================== CREATE ====================

    public static AdvertisementDetailDto createProduct(ProductCreateRequest request) throws Exception {
        String response = api.post("/advs/create-product", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static AdvertisementDetailDto createService(ServiceCreateRequest request) throws Exception {
        String response = api.post("/advs/create-service", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    // ==================== UPDATE (با advId به عنوان پارامتر) ====================

    public static AdvertisementDetailDto updateProduct(String advId, ProductUpdateRequest request) throws Exception {
        String response = api.put("/advs/" + advId + "/update-product", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    public static AdvertisementDetailDto updateService(String advId, ServiceUpdateRequest request) throws Exception {
        String response = api.put("/advs/" + advId + "/update-service", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    // ==================== STATUS & DELETE ====================

    public static String markAsSold(String advId) throws Exception {
        return api.put("/advs/" + advId + "/mark-as-sold", null);
    }

    public static String deleteAdv(String advId) throws Exception {
        return api.delete("/advs/" + advId + "/delete-adv");
    }
}