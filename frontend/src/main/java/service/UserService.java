//package service;
//
//import model.request.UserChangePasswordRequest;
//import model.request.UserUpdateRequest;
//import model.response.UserDto;
//
//public class UserService {
//
//    public UserDto getProfile() throws Exception {
//        String json = ApiClient.get("/user");
//        return ApiClient.fromJson(json, UserDto.class);
//    }
//
//    public UserDto updateProfile(UserUpdateRequest request) throws Exception {
//        String json = ApiClient.put("/user/update-profile", request);
//        return ApiClient.fromJson(json, UserDto.class);
//    }
//
//    public void changePassword(UserChangePasswordRequest request) throws Exception {
//        ApiClient.put("/user/change-password", request);
//    }
//
//    public void deleteAccount() throws Exception {
//        ApiClient.delete("/user/delete-account");
//    }
//}