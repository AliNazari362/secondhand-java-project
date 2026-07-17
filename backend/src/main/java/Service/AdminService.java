package Service;

import DTO.adv.AdvSummaryResponse;
import DTO.user.UserSummaryResponse;
import Entity.Adv;
import Entity.enums.AdvStatus;
import Entity.User;
import Entity.enums.UserStatus;
import Repository.AdvRepository;
import Repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userService::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<UserSummaryResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByUserStatus(status).stream()
                .map(userService::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    public void banUser(UUID userId) {
        User user = userService.findUserById(userId);
        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    public void unbanUser(UUID userId) {
        User user = userService.findUserById(userId);
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    public void approveAdv(UUID advId) {
        advService.approveAdv(advId);
    }

    public void rejectAdv(UUID advId, String reason) {
        advService.rejectAdv(advId, reason);
    }

    public void deleteAdv(UUID advId) {
        Adv adv = advService.findAdvById(advId);
        adv.setStatus(AdvStatus.DELETED);
        advRepository.save(adv);
    }

    public List<AdvSummaryResponse> getPendingAds() {
        return advService.getPendingAds();
    }
}