package com.secondhand.controller;

import com.secondhand.dto.adv.AdvSummaryResponse;
import com.secondhand.dto.user.UserSummaryResponse;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.service.AdminService;
import com.secondhand.service.JwtUtil;
import com.secondhand.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for administrative operations.
 *
 * <p>Provides privileged endpoints for managing users and advertisements.
 * Every endpoint in this controller requires the caller to be an authenticated user with
 * {@link UserType#ADMIN} role; otherwise a {@link ForbiddenException} is thrown.
 * Base path: {@code /api/admin}</p>
 */
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    /**
     * Constructs an {@code AdminController} with the required service dependencies.
     *
     * @param adminService the service containing admin-specific business logic
     * @param userService  the user service used to look up the requesting user for role verification
     */
    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    /**
     * Retrieves a list of all registered users in the system.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a list of {@link UserSummaryResponse} objects representing all users
     */
    @GetMapping("users")
    public List<UserSummaryResponse> getAllUsers(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return adminService.getAllUsers();
    }

    /**
     * Retrieves all users that currently have the given account status.
     *
     * @param status the {@link UserStatus} to filter users by
     * @param token  the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a list of {@link UserSummaryResponse} objects with the specified status
     */
    @GetMapping("get-users-by-status/{status}")
    public List<UserSummaryResponse> getUsersByStatus(@PathVariable UserStatus status,
                                                      @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return adminService.getUsersByStatus(status);
    }

    /**
     * Bans a user account, preventing the user from accessing the platform.
     *
     * @param userId the UUID of the user to ban
     * @param token  the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a success message indicating the user has been banned
     */
    @PutMapping("ban-user/{userId}")
    public ResponseEntity<String> banUser(@PathVariable UUID userId,
                                          @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.banUser(userId);
        return ResponseEntity.ok("کاربر با موفقیت بن شد");
    }

    /**
     * Unbans a previously banned user account, restoring platform access.
     *
     * @param userId the UUID of the user to unban
     * @param token  the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a success message indicating the user has been unbanned
     */
    @PutMapping("unban-user/{userId}")
    public ResponseEntity<String> unbanUser(@PathVariable UUID userId,
                                            @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.unbanUser(userId);
        return ResponseEntity.ok("کاربر با موفقیت آن‌بن شد");
    }

    /**
     * Retrieves all advertisements that are currently awaiting admin approval.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a list of {@link AdvSummaryResponse} objects for pending advertisements
     */
    @GetMapping("get-pendign-ads")
    public List<AdvSummaryResponse> getPendingAds(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return adminService.getPendingAds();
    }

    /**
     * Approves a pending advertisement, making it publicly visible.
     *
     * @param advId the UUID of the advertisement to approve
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a success message indicating the advertisement has been approved
     */
    @PutMapping("approve-adv/{advId}")
    public ResponseEntity<String> approveAdv(@PathVariable UUID advId,
                                             @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.approveAdv(advId);
        return ResponseEntity.ok("آگهی با موفقیت تأیید شد");
    }

    /**
     * Rejects a pending advertisement with an explanatory reason.
     *
     * @param advId  the UUID of the advertisement to reject
     * @param token  the JWT bearer token from the {@code Authorization} request header (admin required)
     * @param reason a plain-text explanation for why the advertisement was rejected
     * @return a success message indicating the advertisement has been rejected
     */
    @PutMapping("reject-adv/{advId}")
    public ResponseEntity<String> rejectAdv(@PathVariable UUID advId,
                                            @RequestHeader("Authorization") String token,
                                            @RequestBody String reason) {
        checkAdmin(token);
        adminService.rejectAdv(advId, reason);
        return ResponseEntity.ok("آگهی با موفقیت رد شد");
    }

    /**
     * Deletes an advertisement from the system regardless of its current status.
     *
     * @param advId the UUID of the advertisement to delete
     * @param token the JWT bearer token from the {@code Authorization} request header (admin required)
     * @return a success message indicating the advertisement has been deleted
     */
    @PutMapping("delete-adv/{advId}")
    public ResponseEntity<String> deleteAdv(@PathVariable UUID advId,
                                            @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.deleteAdv(advId);
        return ResponseEntity.ok("آگهی با موفقیت حذف شد");
    }

    /**
     * Verifies that the token belongs to a user with {@link UserType#ADMIN} role.
     *
     * @param token the JWT bearer token to validate
     * @throws ForbiddenException if the token owner does not have admin privileges
     */
    private void checkAdmin(String token) {
        User admin = userService.findUserById(JwtUtil.getUserIdFromToken(token));
        if (admin.getUserType() != UserType.ADMIN) {
            throw new ForbiddenException("شما دسترسی ادمین برای ورود به این بخش ندارید");
        }
    }
}