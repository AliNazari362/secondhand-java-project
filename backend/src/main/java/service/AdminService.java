package service;

import dto.adv.AdvSummaryResponse;
import dto.user.UserSummaryResponse;
import dto.user.UserChangePasswordRequest;
import entity.Adv;
import entity.enums.AdvStatus;
import entity.User;
import entity.enums.UserStatus;
import entity.enums.UserType;
import Repository.AdvRepository;
import Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final AdvService advService;
    private final AdvRepository advRepository;
    private final UserService userService;

    public AdminService(UserRepository userRepository,
                        AdvService advService,
                        AdvRepository advRepository,
                        UserService userService) {
        this.userRepository = userRepository;
        this.advService = advService;
        this.advRepository = advRepository;
        this.userService = userService;
    }

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

    public List<UserSummaryResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByUserStatus(status).stream()
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

    public void changePassword(UUID userId, UserChangePasswordRequest request) {
        userService.changePassword(userId, request);
    }

    // ---------- مدیریت آگهی‌ها ----------
    public void approveAdv(UUID advId, UUID adminId) {
        checkAdmin(adminId);
        advService.approveAdv(advId);
    }

    public void rejectAdv(UUID advId, String reason, UUID adminId) {
        checkAdmin(adminId);
        advService.rejectAdv(advId, reason);
    }

    public void deleteAdv(UUID advId, UUID adminId) {
        checkAdmin(adminId);
        Adv adv = advService.findAdvById(advId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    public List<AdvSummaryResponse> getPendingAds() {
        return advService.getPendingAds();
    }

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