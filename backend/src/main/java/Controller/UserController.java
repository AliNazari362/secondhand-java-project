package Controller;

import DTO.user.UserChangePasswordRequest;
import DTO.user.UserDetailResponse;
import DTO.user.UserUpdateRequest;
import Service.JwtUtil;
import Service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDetailResponse getProfile(@RequestHeader("Authorization") String token) {
        return userService.getProfile(JwtUtil.getUserIdFromToken(token));
    }

    @PutMapping("update-profile")
    public UserDetailResponse updateProfile(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest request) {
        return userService.updateProfile(JwtUtil.getUserIdFromToken(token), request);
    }

    @PutMapping("change-password")
    public void changePassword(@RequestHeader("Authorization") String token, @RequestBody UserChangePasswordRequest request) {
        userService.changePassword(JwtUtil.getUserIdFromToken(token), request);
    }

    @DeleteMapping("delete-account")
    public void deleteProfile(@RequestHeader("Authorization") String token) {
        userService.deleteProfile(JwtUtil.getUserIdFromToken(token));
    }
}
