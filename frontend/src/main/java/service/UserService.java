package service;

import model.request.UserChangePasswordRequest;
import model.response.UserDto;

public class UserService {

    private final ApiClient api = ApiClient.getInstance();

    public UserDto getProfile() throws Exception {
        String response = api.get("/user/");
        return api.fromJson(response, UserDto.class);
    }

    public String changePassword(UserChangePasswordRequest request) throws Exception {
        return api.put("/user/change-password", request);
    }

    public String deleteAccount() throws Exception {
        return api.delete("user/delete");
    }
}
