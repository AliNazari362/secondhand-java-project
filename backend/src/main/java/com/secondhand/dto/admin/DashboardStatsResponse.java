package com.secondhand.dto.admin;

import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.UserStatus;

/**
 * Response DTO containing statistical data for the admin dashboard.
 *
 * <p>This DTO aggregates various system-wide statistics including user counts,
 * advertisement counts by status, and message/comment totals. It is returned
 * by the admin dashboard endpoint to provide a quick overview of the platform's
 * current state.</p>
 *
 * @param totalUsers     total number of registered users in the system
 * @param activeUsers    number of users with {@link UserStatus#ACTIVE} status
 * @param bannedUsers    number of users with {@link UserStatus#BANNED} status
 * @param deletedUsers   number of users with {@link UserStatus#DELETED} status
 * @param totalAds       total number of advertisements (all statuses)
 * @param pendingAds     number of advertisements with {@link AdvStatus#PENDING} status
 * @param activeAds      number of advertisements with {@link AdvStatus#ACTIVE} status
 * @param soldAds        number of advertisements with {@link AdvStatus#SOLD} status
 * @param rejectedAds    number of advertisements with {@link AdvStatus#REJECTED} status
 * @param totalMessages  total number of messages exchanged in all chatrooms
 * @param totalComments  total number of comments/ratings submitted on advertisements
 */
public record DashboardStatsResponse(
        long totalUsers,
        long activeUsers,
        long bannedUsers,
        long deletedUsers,
        long totalAds,
        long pendingAds,
        long activeAds,
        long soldAds,
        long rejectedAds,
        long totalMessages,
        long totalComments
) {}