package backend.controller;

import backend.api.ApiResponse;
import backend.api.UserDtos;
import backend.security.SecurityUtils;
import backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<UserDtos.UserResponse> me() {
        return ApiResponse.success(userService.me(SecurityUtils.currentUserId()));
    }

    @PutMapping("/settings")
    public ApiResponse<UserDtos.SettingsResponse> updateSettings(@Valid @RequestBody UserDtos.SettingsRequest request) {
        return ApiResponse.success(userService.updateSettings(SecurityUtils.currentUserId(), request));
    }
}
