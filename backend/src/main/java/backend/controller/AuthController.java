package backend.controller;

import backend.api.ApiResponse;
import backend.api.AuthDtos;
import backend.service.AuthService;
import backend.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final String REFRESH_COOKIE = "wordharbor_refresh";
    private final AuthService authService;
    private final RefreshTokenService refreshTokens;
    private final boolean secureCookie;

    public AuthController(AuthService authService, RefreshTokenService refreshTokens,
                          @Value("${app.security.secure-cookie}") boolean secureCookie) {
        this.authService = authService;
        this.refreshTokens = refreshTokens;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDtos.AuthTokens>> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        AuthService.IssuedAuth issued = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, cookie(issued.refreshToken()).toString())
                .body(ApiResponse.success(issued.response()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.AuthTokens>> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        AuthService.IssuedAuth issued = authService.login(request);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie(issued.refreshToken()).toString())
                .body(ApiResponse.success(issued.response()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDtos.RefreshResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        AuthService.RefreshedAuth refreshed = authService.refresh(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie(refreshed.refreshToken()).toString())
                .body(ApiResponse.success(refreshed.response()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        authService.logout(refreshToken);
        ResponseCookie cleared = ResponseCookie.from(REFRESH_COOKIE, "").httpOnly(true).secure(secureCookie)
                .sameSite("Lax").path("/api/auth").maxAge(Duration.ZERO).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cleared.toString())
                .body(ApiResponse.success(null));
    }

    private ResponseCookie cookie(String value) {
        return ResponseCookie.from(REFRESH_COOKIE, value).httpOnly(true).secure(secureCookie)
                .sameSite("Lax").path("/api/auth").maxAge(Duration.ofDays(refreshTokens.refreshTokenDays())).build();
    }
}
