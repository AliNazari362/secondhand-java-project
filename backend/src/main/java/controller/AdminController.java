package controller;

import dto.adv.AdvSummaryResponse;
import dto.user.UserSummaryResponse;
import dto.user.UserChangePasswordRequest;
import entity.User;
import entity.enums.UserStatus;
import entity.enums.UserType;
import exception.AdminPermissionException;
import exception.IllegalTokenException;
import service.AdminService;
import service.JwtUtil;
import service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    // ============================================
    // مدیریت کاربران
    // ============================================

    @GetMapping("/users")
    public List<UserSummaryResponse> getAllUsers(@RequestHeader("Authorization") String header) {
        checkAdmin(header);
        return userService.getAllUsers();
    }

    @GetMapping("/users/status/{status}")
    public List<UserSummaryResponse> getUsersByStatus(@PathVariable UserStatus status,
                                                      @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        return userService.getUsersByStatus(status);
    }

    @PutMapping("/users/{userId}/ban")
    public void banUser(@PathVariable UUID userId,
                        @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        userService.banUser(userId);
    }

    @PutMapping("/users/{userId}/unban")
    public void unbanUser(@PathVariable UUID userId,
                          @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        userService.unbanUser(userId);
    }

    @PutMapping("/users/{userId}/change-password")
    public void changePassword(@PathVariable UUID userId,
                               @Valid @RequestBody UserChangePasswordRequest request,
                               @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        userService.changePassword(userId, request);
    }

    // ============================================
    // مدیریت آگهی‌ها
    // ============================================

    @GetMapping("/ads/pending")
    public List<AdvSummaryResponse> getPendingAds(@RequestHeader("Authorization") String header) {
        checkAdmin(header);
        return adminService.getPendingAds();
    }

    @PutMapping("/ads/{advId}/approve")
    public void approveAdv(@PathVariable UUID advId,
                           @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        UUID adminId = extractUserIdFromToken(header);
        adminService.approveAdv(advId, adminId);
    }

    @PutMapping("/ads/{advId}/reject")
    public void rejectAdv(@PathVariable UUID advId,
                          @RequestBody RejectReasonRequest reason,
                          @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        UUID adminId = extractUserIdFromToken(header);
        adminService.rejectAdv(advId, reason.getReason(), adminId);
    }

    @DeleteMapping("/ads/{advId}")
    public void deleteAdv(@PathVariable UUID advId,
                          @RequestHeader("Authorization") String header) {
        checkAdmin(header);
        UUID adminId = extractUserIdFromToken(header);
        adminService.deleteAdv(advId, adminId);
    }

    // ============================================
    // متدهای کمکی
    // ============================================

    private void checkAdmin(String header) {
        UUID adminId = extractUserIdFromToken(header);
        User admin = userService.findUserById(adminId);
        if (admin.getUserType() != UserType.ADMIN) {
            throw new AdminPermissionException();
        }
    }

    private UUID extractUserIdFromToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalTokenException("توکن نامعتبر است");
        }
        String token = header.substring(7);
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalTokenException("توکن نامعتبر یا منقضی شده است");
        }
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        return UUID.fromString(userIdStr);
    }

    public static class RejectReasonRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}