package Controller;

import DTO.adv.AdvSummaryResponse;
import DTO.user.UserSummaryResponse;
import Entity.User;
import Entity.enums.UserStatus;
import Entity.enums.UserType;
import Service.AdminService;
import Service.JwtUtil;
import Service.UserService;
import SpecialException.AdminPermissionException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("users")
    public List<UserSummaryResponse> getAllUsers(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return adminService.getAllUsers();
    }

    @GetMapping("get-users-by-status/{/status}")
    public List<UserSummaryResponse> getUsersByStatus(@PathVariable UserStatus status, @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return adminService.getUsersByStatus(status);
    }

    @PutMapping("ban-user/{userId}")
    public void banUser(@PathVariable UUID userId, @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.banUser(userId);
    }

    @PutMapping("unban-user/{userId}")
    public void unbanUser(@PathVariable UUID userId, @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.unbanUser(userId);
    }

    @PutMapping("unban-user/{userId}")
    public void changePassword(@PathVariable UUID userId, @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.unbanUser(userId);
    }

    @GetMapping("get-pendign-ads")
    public List<AdvSummaryResponse> getPendingAds(@RequestHeader("Authorization") String token) {
        checkAdmin(token);
        return adminService.getPendingAds();
    }

    @PutMapping("approve-adv/{advId}")
    public void approveAdv(@PathVariable UUID advId, @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.approveAdv(advId);
    }

    @PutMapping("reject-adv/{advId}")
    public void rejectAdv(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody String reason) {
        checkAdmin(token);
        adminService.rejectAdv(advId, reason);
    }

    @PutMapping("delete-adv/{advId}")
    public void deleteAdv(@PathVariable UUID advId, @RequestHeader("Authorization") String token) {
        checkAdmin(token);
        adminService.deleteAdv(advId);
    }

    private void checkAdmin(String token) {
        User admin = userService.findUserById(JwtUtil.getUserIdFromToken(token));
        if (admin.getUserType() != UserType.ADMIN) {
            throw new AdminPermissionException("شما دسترسی ادمین برای ورود به این بخش ندارید");
        }
    }
}
