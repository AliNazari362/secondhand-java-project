package com.secondhand.service;

import com.secondhand.dto.adv.AdvSummaryResponse;
import com.secondhand.dto.admin.DashboardStatsResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.repository.AdvRepository;
import com.secondhand.repository.CommentRepository;
import com.secondhand.repository.MessageRepository;
import com.secondhand.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service providing administrative operations for managing users and advertisements.
 * <p>
 * Admin operations include:
 * <ul>
 *   <li>Listing all users or filtering by status</li>
 *   <li>Banning and unbanning user accounts</li>
 *   <li>Approving, rejecting, and force-deleting advertisements</li>
 *   <li>Retrieving the list of advertisements pending review</li>
 *   <li>Retrieving system-wide statistics for the admin dashboard</li>
 * </ul>
 * These operations should only be accessible to users with an admin role.
 * </p>
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AdvService advService;
    private final AdvRepository advRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final MessageRepository messageRepository;

    /**
     * Constructs an {@code AdminService} with all required repository and service dependencies.
     *
     * @param userRepository     the repository for persisting and querying {@link User} entities
     * @param advService         the service used for advertisement operations and lookups
     * @param advRepository      the repository for persisting and querying {@link Adv} entities
     * @param userService        the service used for user lookups and response mapping
     * @param commentRepository  the repository for querying {@link com.secondhand.entity.Comment} entities
     * @param messageRepository  the repository for querying {@link com.secondhand.entity.Message} entities
     */
    public AdminService(UserRepository userRepository,
                        AdvService advService,
                        AdvRepository advRepository,
                        UserService userService,
                        CommentRepository commentRepository,
                        MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.advService = advService;
        this.advRepository = advRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Returns a summary list of all registered users in the system.
     *
     * @return a list of {@link UserSummaryResponse} for every user; never {@code null}, may be empty
     */
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userService::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns a summary list of all users with the given account status.
     *
     * @param status the {@link UserStatus} to filter by (e.g., {@code ACTIVE}, {@code BANNED})
     * @return a list of {@link UserSummaryResponse} objects for users with the given status;
     *         never {@code null}, may be empty
     */
    public List<UserSummaryResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByUserStatus(status).stream()
                .map(userService::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Bans a user account by setting its status to {@link UserStatus#BANNED}.
     * <p>
     * A banned user will be blocked from logging in.
     * </p>
     *
     * @param userId the UUID of the user to ban
     * @throws com.secondhand.exception.ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public void banUser(UUID userId) {
        User user = userService.findUserById(userId);
        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    /**
     * Lifts the ban on a user account by restoring its status to {@link UserStatus#ACTIVE}.
     *
     * @param userId the UUID of the user to unban
     * @throws com.secondhand.exception.ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public void unbanUser(UUID userId) {
        User user = userService.findUserById(userId);
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    /**
     * Approves a pending advertisement, making it publicly visible.
     * <p>
     * Delegates to {@link AdvService#approveAdv(UUID)}.
     * </p>
     *
     * @param advId the UUID of the advertisement to approve
     * @throws com.secondhand.exception.ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws com.secondhand.exception.BadRequestException       if the advertisement is not in pending status
     */
    public void approveAdv(UUID advId) {
        advService.approveAdv(advId);
    }

    /**
     * Rejects a pending advertisement and records the reason for rejection.
     * <p>
     * Delegates to {@link AdvService#rejectAdv(UUID, String)}.
     * </p>
     *
     * @param advId  the UUID of the advertisement to reject
     * @param reason the explanation for why the advertisement was rejected
     * @throws com.secondhand.exception.ResourceNotFoundException if no advertisement exists with the given {@code advId}
     * @throws com.secondhand.exception.BadRequestException       if the advertisement is not in pending status
     */
    public void rejectAdv(UUID advId, String reason) {
        advService.rejectAdv(advId, reason);
    }

    /**
     * Force-deletes an advertisement by setting its status to {@link AdvStatus#DELETED},
     * regardless of the advertisement's current owner.
     * <p>
     * Unlike the user-facing delete, this operation does not validate ownership,
     * allowing admins to remove any advertisement.
     * </p>
     *
     * @param advId the UUID of the advertisement to delete
     * @throws com.secondhand.exception.ResourceNotFoundException if no advertisement exists with the given {@code advId}
     */
    public void deleteAdv(UUID advId) {
        Adv adv = advService.findAdvById(advId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    /**
     * Returns a summary list of all advertisements currently awaiting admin review.
     * <p>
     * Delegates to {@link AdvService#getPendingAds()}.
     * </p>
     *
     * @return a list of {@link AdvSummaryResponse} objects for pending advertisements;
     *         never {@code null}, may be empty
     */
    public List<AdvSummaryResponse> getPendingAds() {
        return advService.getPendingAds();
    }

    /**
     * Retrieves comprehensive system statistics for the admin dashboard.
     * <p>
     * This method aggregates data from multiple repositories to provide a
     * complete overview of the platform's current state, including user counts,
     * advertisement distribution by status, and total message/comment counts.
     * </p>
     *
     * @return a {@link DashboardStatsResponse} containing all aggregated statistics
     */
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByUserStatus(UserStatus.ACTIVE).size();
        long bannedUsers = userRepository.findByUserStatus(UserStatus.BANNED).size();
        long deletedUsers = userRepository.findByUserStatus(UserStatus.DELETED).size();

        long totalAds = advRepository.count();
        long pendingAds = advRepository.findByStatus(AdvStatus.PENDING).size();
        long activeAds = advRepository.findByStatus(AdvStatus.ACTIVE).size();
        long soldAds = advRepository.findByStatus(AdvStatus.SOLD).size();
        long rejectedAds = advRepository.findByStatus(AdvStatus.REJECTED).size();

        long totalMessages = messageRepository.count();
        long totalComments = commentRepository.count();

        return new DashboardStatsResponse(
                totalUsers,
                activeUsers,
                bannedUsers,
                deletedUsers,
                totalAds,
                pendingAds,
                activeAds,
                soldAds,
                rejectedAds,
                totalMessages,
                totalComments
        );
    }
}