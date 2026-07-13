package backend.api;

import backend.domain.PreferredLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public final class UserDtos {
    private UserDtos() {}

    public record SettingsRequest(
            @Min(5) @Max(100) int dailyNewWords,
            @Min(20) @Max(300) int dailyReviewLimit,
            @NotNull PreferredLevel preferredLevel,
            @NotNull @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$") String reminderTime,
            boolean soundEnabled) {}

    public record SettingsResponse(int dailyNewWords, int dailyReviewLimit, PreferredLevel preferredLevel,
                                   String reminderTime, boolean soundEnabled) {}

    public record UserResponse(long id, String username, String nickname, String email, String avatarText,
                               int continuousDays, SettingsResponse settings) {}
}
