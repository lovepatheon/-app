package backend.security;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long accessTokenSeconds;

    public JwtService(ObjectMapper objectMapper,
                      @Value("${app.security.jwt-secret}") String secret,
                      @Value("${app.security.access-token-seconds}") long accessTokenSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSeconds = accessTokenSeconds;
    }

    public String issue(long userId, String username) {
        try {
            long now = Instant.now().getEpochSecond();
            String header = encode(objectMapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String payload = encode(objectMapper.writeValueAsBytes(Map.of(
                    "sub", Long.toString(userId), "username", username, "iat", now, "exp", now + accessTokenSeconds)));
            String unsigned = header + "." + payload;
            return unsigned + "." + encode(sign(unsigned));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to issue access token", ex);
        }
    }

    public AuthenticatedUser parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Malformed token");
            byte[] expected = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expected, DECODER.decode(parts[2]))) throw new IllegalArgumentException("Invalid signature");
            JsonNode payload = objectMapper.readTree(DECODER.decode(parts[1]));
            if (payload.path("exp").asLong() <= Instant.now().getEpochSecond()) throw new IllegalArgumentException("Expired token");
            return new AuthenticatedUser(Long.parseLong(payload.path("sub").asText()), payload.path("username").asText());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid access token", ex);
        }
    }

    public long expiresIn() { return accessTokenSeconds; }

    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(byte[] bytes) { return ENCODER.encodeToString(bytes); }
}
