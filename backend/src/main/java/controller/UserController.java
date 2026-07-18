package controller;

import dto.user.UserDetailResponse;
import dto.user.UserUpdateRequest;
import dto.user.UserChangePasswordRequest;
import exception.IllegalTokenException;
import service.JwtUtil;
import service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserDetailResponse getProfile(@RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        return userService.getProfile(userId);
    }

    @PutMapping("/profile")
    public UserDetailResponse updateProfile(@RequestHeader("Authorization") String header,
                                            @Valid @RequestBody UserUpdateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return userService.updateProfile(userId, request);
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestHeader("Authorization") String header,
                               @Valid @RequestBody UserChangePasswordRequest request) {
        UUID userId = extractUserIdFromToken(header);
        userService.changePassword(userId, request);
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
}