package backend.service;

import backend.api.AuthDtos;
import backend.domain.User;
import backend.domain.UserSettings;
import backend.exception.BusinessException;
import backend.repository.UserRepository;
import backend.repository.UserSettingsRepository;
import backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final UserSettingsRepository settings;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokens;
    private final UserService userService;

    public AuthService(UserRepository users, UserSettingsRepository settings, PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokens, UserService userService) {
        this.users = users;
        this.settings = settings;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
        this.userService = userService;
    }

    @Transactional
    public IssuedAuth register(AuthDtos.RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();
        if (users.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException(11001, HttpStatus.CONFLICT, "用户名已存在");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(11002, HttpStatus.CONFLICT, "邮箱已存在");
        }
        User user = users.save(new User(username, email, passwordEncoder.encode(request.password()), request.nickname().trim()));
        settings.save(new UserSettings(user));
        return issue(user);
    }

    @Transactional
    public IssuedAuth login(AuthDtos.LoginRequest request) {
        User user = users.findByUsernameIgnoreCase(request.username().trim())
                .filter(candidate -> "ACTIVE".equals(candidate.getStatus()))
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(() -> new BusinessException(11003, HttpStatus.UNAUTHORIZED, "账号或密码错误"));
        return issue(user);
    }

    @Transactional
    public RefreshedAuth refresh(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new BusinessException(11004, HttpStatus.UNAUTHORIZED, "登录会话失效");
        }
        RefreshTokenService.RotatedToken rotated = refreshTokens.rotate(rawRefreshToken);
        return new RefreshedAuth(new AuthDtos.RefreshResponse(
                jwtService.issue(rotated.user().getId(), rotated.user().getUsername()), jwtService.expiresIn()),
                rotated.rawToken());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokens.revoke(rawRefreshToken);
    }

    private IssuedAuth issue(User user) {
        String accessToken = jwtService.issue(user.getId(), user.getUsername());
        String refreshToken = refreshTokens.issue(user);
        AuthDtos.AuthTokens response = new AuthDtos.AuthTokens(accessToken, jwtService.expiresIn(), userService.toResponse(user));
        return new IssuedAuth(response, refreshToken);
    }

    public record IssuedAuth(AuthDtos.AuthTokens response, String refreshToken) {}
    public record RefreshedAuth(AuthDtos.RefreshResponse response, String refreshToken) {}
}
