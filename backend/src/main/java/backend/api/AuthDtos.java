package backend.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank @Size(max = 50) String username,
            @NotBlank @Size(max = 72) String password) {}

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 1, max = 50) String nickname,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 6, max = 72) String password) {}

    public record AuthTokens(String accessToken, long expiresIn, UserDtos.UserResponse user) {}
    public record RefreshResponse(String accessToken, long expiresIn) {}
}
