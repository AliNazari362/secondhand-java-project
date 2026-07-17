package Controller;

import DTO.user.UserChangePasswordRequest;
import DTO.user.UserDetailResponse;
import DTO.user.UserUpdateRequest;
import Service.JwtUtil;
import Service.UserService;
import SpecialException.IllegalTokenException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDetailResponse getProfile(@RequestHeader("Authorization") String token) {
        return userService.getProfile(getUserId(token));
    }

    @PutMapping("update-profile")
    public UserDetailResponse updateProfile(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest request) {
        return userService.updateProfile(getUserId(token), request);
    }

    @PutMapping("change-password")
    public void changePassword(@RequestHeader("Authorization") String token, @RequestBody UserChangePasswordRequest request) {
        userService.changePassword(getUserId(token), request);
    }

    @DeleteMapping("delete-account")
    public void deleteProfile(@RequestHeader("Authorization") String token) {
        userService.deleteProfile(getUserId(token));
    }

    private UUID getUserId(String token) {
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalTokenException("درخواست ارسالی معتبر نیست");
        }
        return UUID.fromString(JwtUtil.getUserIdFromToken(token));
    }
}
