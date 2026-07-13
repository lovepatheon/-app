package backend.api;

import java.util.List;

public final class StatisticsDtos {
    private StatisticsDtos() {}

    public record DailyPoint(String date, String label, long learned, long reviewed) {}
    public record MasterySlice(String label, long value, String color) {}
    public record Achievement(String title, String description, String date) {}
    public record OverviewResponse(String range, long totalLearned, long totalMastered, int reviewAccuracy,
                                   long totalMinutes, List<DailyPoint> daily,
                                   List<MasterySlice> masteryDistribution,
                                   List<Achievement> recentAchievements) {}
}
