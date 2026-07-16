package Service;

import DTO.adv.AdvSummaryResponse;
import DTO.user.UserSummaryResponse;
import Entity.Adv;
import Entity.enums.AdvStatus;
import Entity.User;
import Entity.enums.UserStatus;
import Entity.enums.UserType;
import Repository.AdvRepository;
import Repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminService {

    private final UserRepository userRepository;
    private final AdvService advService;
    private final AdvRepository advRepository;
    private final UserService userService; // 🔥 این را اضافه کن

    public AdminService(UserRepository userRepository,
                        AdvService advService,
                        AdvRepository advRepository,
                        UserService userService) { // 🔥 پارامتر را اضافه کن
        this.userRepository = userRepository;
        this.advService = advService;
        this.advRepository = advRepository;
        this.userService = userService;
    }

    // 🔥 متد کمکی برای چک کردن ادمین
    private void checkAdmin(UUID adminId) {
        User admin = userService.findUserById(adminId);
        if (admin.getUserType() != UserType.ADMIN) {
            throw new RuntimeException("Admin access required");
        }
    }

    // ---------- مدیریت کاربران ----------
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    public void banUser(UUID userId) {
        User user = findUserById(userId);
        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    public void unbanUser(UUID userId) {
        User user = findUserById(userId);
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    // ---------- مدیریت آگهی‌ها (ادمین) ----------
    public void approveAdv(UUID advId, UUID adminId) { // 🔥 adminId اضافه شد
        checkAdmin(adminId);
        advService.approveAdv(advId);
    }

    public void rejectAdv(UUID advId, String reason, UUID adminId) { // 🔥 adminId اضافه شد
        checkAdmin(adminId);
        advService.rejectAdv(advId, reason);
    }

    public void deleteAdv(UUID advId, UUID adminId) { // 🔥 adminId اضافه شد
        checkAdmin(adminId);
        Adv adv = advService.findAdvById(advId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    public List<AdvSummaryResponse> getPendingAds() {
        return advService.getPendingAds();
    }

    // ---------- متدهای کمکی ----------
    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType()
        );
    }
}