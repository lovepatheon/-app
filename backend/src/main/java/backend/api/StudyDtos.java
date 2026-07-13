package backend.api;

import backend.domain.MasteryLevel;
import backend.domain.RecallRating;
import backend.domain.StudyMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;
import java.util.List;

public final class StudyDtos {
    private StudyDtos() {}

    public record WeeklyActivity(String date, String label, long count, int target) {}
    public record DashboardResponse(String greeting, long learnedToday, int newWordTarget, long reviewedToday,
                                    long reviewDue, long masteredTotal, int continuousDays,
                                    List<WeeklyActivity> weeklyActivity,
                                    List<WordDtos.SummaryResponse> difficultWords) {}

    public record QueueResponse(String sessionId, StudyMode mode, int total, int completed,
                                List<WordDtos.DetailResponse> words) {}

    public record AnswerRequest(
            @NotBlank String sessionId,
            @NotNull @Positive Long wordId,
            @NotNull RecallRating rating,
            @Min(0) @Max(3600000) int responseTimeMs) {}

    public record AnswerResponse(OffsetDateTime nextReviewAt, int intervalDays, MasteryLevel mastery) {}
}
