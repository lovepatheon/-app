package backend.service;

import backend.domain.RefreshToken;
import backend.domain.User;
import backend.exception.BusinessException;
import backend.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository tokens;
    private final SecureRandom random = new SecureRandom();
    private final int refreshTokenDays;

    public RefreshTokenService(RefreshTokenRepository tokens,
                               @Value("${app.security.refresh-token-days}") int refreshTokenDays) {
        this.tokens = tokens;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public String issue(User user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.save(new RefreshToken(user, hash(raw), Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS)));
        return raw;
    }

    @Transactional
    public RotatedToken rotate(String raw) {
        RefreshToken current = tokens.findByTokenHash(hash(raw))
                .filter(RefreshToken::isUsable)
                .orElseThrow(() -> new BusinessException(11004, HttpStatus.UNAUTHORIZED, "登录会话失效"));
        current.revoke();
        String next = issue(current.getUser());
        return new RotatedToken(current.getUser(), next);
    }

    @Transactional
    public void revoke(String raw) {
        if (raw == null || raw.isBlank()) return;
        tokens.findByTokenHash(hash(raw)).ifPresent(RefreshToken::revoke);
    }

    public int refreshTokenDays() { return refreshTokenDays; }

    private static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public record RotatedToken(User user, String rawToken) {}
}
