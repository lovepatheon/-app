package backend.service;

import backend.api.StatisticsDtos;
import backend.domain.*;
import backend.exception.BusinessException;
import backend.repository.ReviewAnswerRepository;
import backend.repository.UserWordProgressRepository;
import backend.repository.WordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {
    private final ReviewAnswerRepository answers;
    private final UserWordProgressRepository progress;
    private final WordRepository words;
    private final ActivityService activityService;

    public StatisticsService(ReviewAnswerRepository answers, UserWordProgressRepository progress,
                             WordRepository words, ActivityService activityService) {
        this.answers = answers;
        this.progress = progress;
        this.words = words;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public StatisticsDtos.OverviewResponse overview(long userId, String range) {
        int days = switch (range) {
            case "7D" -> 7;
            case "30D" -> 30;
            default -> throw new BusinessException(10001, HttpStatus.BAD_REQUEST, "range 只能是 7D 或 30D");
        };
        LocalDate today = LocalDate.now(DateTimes.ZONE);
        LocalDate first = today.minusDays(days - 1L);
        List<ReviewAnswer> period = answers.findActivity(userId, DateTimes.startOfDay(first), DateTimes.startOfDay(today.plusDays(1)));

        Map<LocalDate, List<ReviewAnswer>> byDate = new HashMap<>();
        for (ReviewAnswer answer : period) {
            LocalDate date = answer.getCreatedAt().atZone(DateTimes.ZONE).toLocalDate();
            byDate.computeIfAbsent(date, ignored -> new ArrayList<>()).add(answer);
        }
        List<StatisticsDtos.DailyPoint> daily = new ArrayList<>();
        for (int offset = 0; offset < days; offset++) {
            LocalDate date = first.plusDays(offset);
            List<ReviewAnswer> values = byDate.getOrDefault(date, List.of());
            long learned = values.stream().filter(a -> a.getSession().getMode() == StudyMode.NEW).count();
            long reviewed = values.stream().filter(a -> a.getSession().getMode() == StudyMode.REVIEW).count();
            String label = date.equals(today) ? "今天" : "周" + "一二三四五六日".charAt(date.getDayOfWeek().getValue() - 1);
            daily.add(new StatisticsDtos.DailyPoint(String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth()),
                    label, learned, reviewed));
        }

        long reviewedCount = period.stream().filter(a -> a.getSession().getMode() == StudyMode.REVIEW).count();
        long successful = period.stream().filter(a -> a.getSession().getMode() == StudyMode.REVIEW)
                .filter(a -> a.getRating() == RecallRating.GOOD || a.getRating() == RecallRating.EASY).count();
        int accuracy = reviewedCount == 0 ? 0 : (int) Math.round(successful * 100.0 / reviewedCount);
        long learned = period.stream().filter(a -> a.getSession().getMode() == StudyMode.NEW).count();
        long minutes = Math.round(period.stream().mapToLong(ReviewAnswer::getResponseTimeMs).sum() / 60000.0);

        Map<MasteryLevel, Long> counts = new EnumMap<>(MasteryLevel.class);
        progress.countMastery(userId).forEach(row -> counts.put((MasteryLevel) row[0], (Long) row[1]));
        long unseen = Math.max(0, words.count() - progress.countByUserId(userId));
        counts.merge(MasteryLevel.NEW, unseen, Long::sum);
        List<StatisticsDtos.MasterySlice> distribution = List.of(
                new StatisticsDtos.MasterySlice("已掌握", counts.getOrDefault(MasteryLevel.MASTERED, 0L), "#2f7668"),
                new StatisticsDtos.MasterySlice("熟悉", counts.getOrDefault(MasteryLevel.FAMILIAR, 0L), "#7e9f78"),
                new StatisticsDtos.MasterySlice("学习中", counts.getOrDefault(MasteryLevel.LEARNING, 0L), "#e9a23b"),
                new StatisticsDtos.MasterySlice("新词", counts.getOrDefault(MasteryLevel.NEW, 0L), "#e8ded0"));

        int streak = activityService.continuousDays(userId);
        List<StatisticsDtos.Achievement> achievements = new ArrayList<>();
        if (streak > 0) achievements.add(new StatisticsDtos.Achievement(
                "连续学习 " + streak + " 天", "学习节奏已经开始稳定下来", "今天"));
        long mastered = progress.countByUserIdAndMastery(userId, MasteryLevel.MASTERED);
        if (mastered > 0) achievements.add(new StatisticsDtos.Achievement(
                "已掌握 " + mastered + " 词", "继续保持稳定复习", "今天"));

        return new StatisticsDtos.OverviewResponse(range, learned, mastered, accuracy, minutes,
                daily, distribution, achievements);
    }
}
