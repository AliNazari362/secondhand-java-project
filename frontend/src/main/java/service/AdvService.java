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

/**
 * Service class for advertisement-related API operations.
 * Provides methods for searching, creating, updating, and deleting advertisements.
 */
public class AdvService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Searches for active advertisements with optional filters.
     *
     * @param keyword    optional keyword for title/description search
     * @param city       optional city filter
     * @param categoryId optional category ID filter
     * @param sortBy     sorting criterion (newest, oldest, priceAsc, priceDesc, ratingDesc)
     * @param minPrice   optional minimum price (only for products)
     * @param maxPrice   optional maximum price (only for products)
     * @return list of matching advertisements
     * @throws Exception if the API call fails
     */
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

    /**
     * Retrieves the full detail of an advertisement by its ID.
     *
     * @param advId the advertisement ID
     * @return the advertisement detail DTO
     * @throws Exception if the API call fails
     */
    public static AdvertisementDetailDto getAdvDetail(String advId) throws Exception {
        String response = api.get("/advs/" + advId);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    /**
     * Retrieves all advertisements belonging to a specific user.
     *
     * @param userId the user ID
     * @return list of advertisement summaries for the user
     * @throws Exception if the API call fails
     */
    public static List<AdvertisementSummaryDto> getUserAds(String userId) throws Exception {
        String response = api.get("/advs/user/" + userId);
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    /**
     * Creates a new product advertisement.
     *
     * @param request the product creation request
     * @return the created advertisement detail
     * @throws Exception if the API call fails
     */
    public static AdvertisementDetailDto createProduct(ProductCreateRequest request) throws Exception {
        String response = api.post("/advs/create-product", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    /**
     * Creates a new service advertisement.
     *
     * @param request the service creation request
     * @return the created advertisement detail
     * @throws Exception if the API call fails
     */
    public static AdvertisementDetailDto createService(ServiceCreateRequest request) throws Exception {
        String response = api.post("/advs/create-service", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    /**
     * Updates an existing product advertisement.
     *
     * @param advId   the advertisement ID to update
     * @param request the product update request
     * @return the updated advertisement detail
     * @throws Exception if the API call fails
     */
    public static AdvertisementDetailDto updateProduct(String advId, ProductUpdateRequest request) throws Exception {
        String response = api.put("/advs/" + advId + "/update-product", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    /**
     * Updates an existing service advertisement.
     *
     * @param advId   the advertisement ID to update
     * @param request the service update request
     * @return the updated advertisement detail
     * @throws Exception if the API call fails
     */
    public static AdvertisementDetailDto updateService(String advId, ServiceUpdateRequest request) throws Exception {
        String response = api.put("/advs/" + advId + "/update-service", request);
        return api.fromJson(response, AdvertisementDetailDto.class);
    }

    /**
     * Marks an advertisement as sold.
     *
     * @param advId the advertisement ID
     * @return the API response string
     * @throws Exception if the API call fails
     */
    public static String markAsSold(String advId) throws Exception {
        return api.put("/advs/" + advId + "/mark-as-sold", null);
    }

    /**
     * Deletes an advertisement by its ID.
     *
     * @param advId the advertisement ID
     * @return the API response string
     * @throws Exception if the API call fails
     */
    public static String deleteAdv(String advId) throws Exception {
        return api.delete("/advs/" + advId + "/delete-adv");
    }
}