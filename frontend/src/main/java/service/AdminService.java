package service;

import model.DashboardStats;
import model.response.AdvertisementSummaryDto;
import model.response.UserSummaryDto;

import java.util.List;

public class AdminService {

    private final ApiClient api = ApiClient.getInstance();

    public List<UserSummaryDto> getAllUsers() throws Exception {
        String response = api.get("/admin/users");
        UserSummaryDto[] users = api.fromJson(response, UserSummaryDto[].class);
        return List.of(users);
    }

    public List<UserSummaryDto> getUsersByStatus(String status) throws Exception {
        String response = api.get("/admin/get-users-by-status/" + status);
        UserSummaryDto[] users = api.fromJson(response, UserSummaryDto[].class);
        return List.of(users);
    }

    public String banUser(String userId) throws Exception {
        return api.put("/admin/ban-user/" + userId, null);
    }

    public String unbanUser(String userId) throws Exception {
        return api.put("/admin/unban-user/" + userId, null);
    }

    public List<AdvertisementSummaryDto> getPendingAds() throws Exception {
        String response = api.get("/admin/get-pending-ads");
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    public String approveAdv(String advId) throws Exception {
        return api.put("/admin/approve-adv/" + advId, null);
    }

    public String rejectAdv(String advId) throws Exception {
        return api.put("/admin/reject-adv/" + advId, null);
    }

    public String deleteAdv(String advId) throws Exception {
        return api.delete("/admin/delete-adv/" + advId);
    }

    public DashboardStats getDashboardStats() throws Exception {
        String response = api.get("/admin/dashboard-stats");
        return api.fromJson(response, DashboardStats.class);
    }
}