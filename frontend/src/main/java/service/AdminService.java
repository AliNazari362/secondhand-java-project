package service;

import model.DashboardStats;
import model.response.AdvertisementSummaryDto;
import model.response.UserSummaryDto;

import java.util.List;

/**
 * Service class for admin-related API operations.
 * Provides methods for user management, advertisement moderation, and dashboard statistics.
 */
public class AdminService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves all users from the system.
     *
     * @return a list of all user summaries
     * @throws Exception if the API request fails
     */
    public static List<UserSummaryDto> getAllUsers() throws Exception {
        String response = api.get("/admin/users");
        UserSummaryDto[] users = api.fromJson(response, UserSummaryDto[].class);
        return List.of(users);
    }

    /**
     * Retrieves users filtered by their account status.
     *
     * @param status the user status to filter by
     * @return a list of user summaries matching the given status
     * @throws Exception if the API request fails
     */
    public static List<UserSummaryDto> getUsersByStatus(String status) throws Exception {
        String response = api.get("/admin/get-users-by-status/" + status);
        UserSummaryDto[] users = api.fromJson(response, UserSummaryDto[].class);
        return List.of(users);
    }

    /**
     * Bans a user by their ID.
     *
     * @param userId the ID of the user to ban
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String banUser(String userId) throws Exception {
        return api.put("/admin/ban-user/" + userId, null);
    }

    /**
     * Unbans a previously banned user by their ID.
     *
     * @param userId the ID of the user to unban
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String unbanUser(String userId) throws Exception {
        return api.put("/admin/unban-user/" + userId, null);
    }

    /**
     * Retrieves all pending advertisements awaiting approval.
     *
     * @return a list of pending advertisement summaries
     * @throws Exception if the API request fails
     */
    public static List<AdvertisementSummaryDto> getPendingAds() throws Exception {
        String response = api.get("/admin/get-pending-ads");
        AdvertisementSummaryDto[] ads = api.fromJson(response, AdvertisementSummaryDto[].class);
        return List.of(ads);
    }

    /**
     * Approves a pending advertisement.
     *
     * @param advId the ID of the advertisement to approve
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String approveAdv(String advId) throws Exception {
        return api.put("/admin/approve-adv/" + advId, null);
    }

    /**
     * Rejects a pending advertisement.
     *
     * @param advId the ID of the advertisement to reject
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String rejectAdv(String advId) throws Exception {
        return api.put("/admin/reject-adv/" + advId, null);
    }

    /**
     * Deletes an advertisement by its ID.
     *
     * @param advId the ID of the advertisement to delete
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String deleteAdv(String advId) throws Exception {
        return api.delete("/admin/delete-adv/" + advId);
    }

    /**
     * Retrieves dashboard statistics including user counts, ad counts, and message totals.
     *
     * @return the dashboard statistics object
     * @throws Exception if the API request fails
     */
    public static DashboardStats getDashboardStats() throws Exception {
        String response = api.get("/admin/dashboard-stats");
        return api.fromJson(response, DashboardStats.class);
    }
}