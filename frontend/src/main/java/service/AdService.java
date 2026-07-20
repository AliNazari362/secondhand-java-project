//package service;
//
//import model.request.*;
//import model.response.AdvertisementDetailDto;
//import model.response.AdvertisementSummaryDto;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//public class AdService {
//
//    public List<AdvertisementSummaryDto> getActiveAds(String keyword, String city) throws Exception {
//        String url = "/advs/search";
//        if (keyword != null && !keyword.isBlank()) url += "?keyword=" + keyword;
//        if (city != null && !city.isBlank()) url += (keyword == null ? "?city=" : "&city=") + city;
//        String json = ApiClient.get(url);
//        return Arrays.asList(ApiClient.fromJson(json, AdvertisementSummaryDto[].class));
//    }
//
//    public AdvertisementDetailDto getAdDetail(UUID advId) throws Exception {
//        String json = ApiClient.get("/advs/" + advId);
//        return ApiClient.fromJson(json, AdvertisementDetailDto.class);
//    }
//
//    public AdvertisementDetailDto createProduct(ProductCreateRequest request) throws Exception {
//        String json = ApiClient.post("/advs/create-product", request);
//        return ApiClient.fromJson(json, AdvertisementDetailDto.class);
//    }
//
//    public AdvertisementDetailDto createService(ServiceCreateRequest request) throws Exception {
//        String json = ApiClient.post("/advs/create-service", request);
//        return ApiClient.fromJson(json, AdvertisementDetailDto.class);
//    }
//
//    public AdvertisementDetailDto updateProduct(UUID advId, ProductUpdateRequest request) throws Exception {
//        String json = ApiClient.put("/advs/" + advId + "/update-product", request);
//        return ApiClient.fromJson(json, AdvertisementDetailDto.class);
//    }
//
//    public AdvertisementDetailDto updateService(UUID advId, ServiceUpdateRequest request) throws Exception {
//        String json = ApiClient.put("/advs/" + advId + "/update-service", request);
//        return ApiClient.fromJson(json, AdvertisementDetailDto.class);
//    }
//
//    public void deleteAd(UUID advId) throws Exception {
//        ApiClient.delete("/advs/" + advId + "/delete-adv");
//    }
//
//    public void markAsSold(UUID advId) throws Exception {
//        ApiClient.put("/advs/" + advId + "/mark-as-sold", null);
//    }
//}