package backend.service;

import backend.api.StudyDtos;
import backend.api.WordDtos;
import backend.domain.*;
import backend.exception.BusinessException;
import backend.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudyService {
    private final UserRepository users;
    private final UserSettingsRepository settings;
    private final WordRepository words;
    private final UserWordProgressRepository progress;
    private final StudySessionRepository sessions;
    private final ReviewAnswerRepository answers;
    private final WordService wordService;
    private final ActivityService activityService;

    public StudyService(UserRepository users, UserSettingsRepository settings, WordRepository words,
                        UserWordProgressRepository progress, StudySessionRepository sessions,
                        ReviewAnswerRepository answers, WordService wordService, ActivityService activityService) {
        this.users = users;
        this.settings = settings;
        this.words = words;
        this.progress = progress;
        this.sessions = sessions;
        this.answers = answers;
        this.wordService = wordService;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public StudyDtos.DashboardResponse dashboard(long userId) {
        UserSettings userSettings = requireSettings(userId);
        LocalDate today = LocalDate.now(DateTimes.ZONE);
        Instant todayStart = DateTimes.startOfDay(today);
        Instant tomorrowStart = DateTimes.startOfDay(today.plusDays(1));
        long learnedToday = answers.countByModeAndPeriod(userId, StudyMode.NEW, todayStart, tomorrowStart);
        long reviewedToday = answers.countByModeAndPeriod(userId, StudyMode.REVIEW, todayStart, tomorrowStart);

        List<StudyDtos.WeeklyActivity> weekly = new ArrayList<>();
        String[] labels = {"一", "二", "三", "四", "五", "六", "日"};
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate date = today.minusDays(offset);
            long count = answers.countByModeAndPeriod(userId, StudyMode.NEW,
                    DateTimes.startOfDay(date), DateTimes.startOfDay(date.plusDays(1)));
            weekly.add(new StudyDtos.WeeklyActivity(String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth()),
                    labels[date.getDayOfWeek().getValue() - 1], count, userSettings.getDailyNewWords()));
        }

        List<WordDtos.SummaryResponse> difficult = progress
                .findTop10ByUserIdAndMasteryOrderByLapseCountDescUpdatedAtAsc(userId, MasteryLevel.LEARNING).stream()
                .limit(3).map(item -> wordService.toSummary(item.getWord(), item)).toList();

        return new StudyDtos.DashboardResponse(
                "把今天的 " + userSettings.getDailyNewWords() + " 个新词，变成明天的熟词。",
                learnedToday, userSettings.getDailyNewWords(), reviewedToday,
                progress.countByUserIdAndNextReviewAtLessThanEqual(userId, Instant.now()),
                progress.countByUserIdAndMastery(userId, MasteryLevel.MASTERED),
                activityService.continuousDays(userId), weekly, difficult);
    }

    @Transactional
    public StudyDtos.QueueResponse queue(long userId, StudyMode mode) {
        Optional<StudySession> active = sessions.findFirstByUserIdAndModeAndStatusOrderByCreatedAtDesc(
                userId, mode, SessionStatus.ACTIVE);
        if (active.isPresent()) return toQueue(active.get(), userId);

        User user = users.findById(userId)
                .orElseThrow(() -> new BusinessException(11004, HttpStatus.UNAUTHORIZED, "登录会话失效"));
        UserSettings userSettings = requireSettings(userId);
        WordLevel level = preferredLevel(userSettings.getPreferredLevel());
        int limit = mode == StudyMode.NEW ? userSettings.getDailyNewWords() : userSettings.getDailyReviewLimit();
        List<Word> queueWords = mode == StudyMode.NEW
                ? words.findNewQueue(userId, level, PageRequest.of(0, limit))
                : words.findReviewQueue(userId, level, Instant.now(), PageRequest.of(0, limit));
        if (mode == StudyMode.REVIEW && queueWords.isEmpty()) {
            throw new BusinessException(13003, HttpStatus.UNPROCESSABLE_ENTITY, "今日没有待复习单词");
        }
        StudySession session = sessions.save(new StudySession(UUID.randomUUID().toString(), user, mode, queueWords));
        return toQueue(session, userId);
    }

    @Transactional
    public StudyDtos.AnswerResponse answer(long userId, StudyDtos.AnswerRequest request) {
        StudySession session = sessions.findByIdAndUserId(request.sessionId(), userId)
                .orElseThrow(() -> new BusinessException(13001, HttpStatus.NOT_FOUND, "学习会话不存在或已结束"));
        Word word = session.getWords().stream().filter(item -> item.getId().equals(request.wordId())).findFirst()
                .orElseThrow(() -> new BusinessException(13001, HttpStatus.UNPROCESSABLE_ENTITY, "该单词不属于当前学习会话"));

        Optional<ReviewAnswer> duplicate = answers.findByUserIdAndSessionIdAndWordId(userId, request.sessionId(), request.wordId());
        if (duplicate.isPresent()) return toAnswer(duplicate.get());
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new BusinessException(13001, HttpStatus.UNPROCESSABLE_ENTITY, "学习会话不存在或已结束");
        }

        User user = users.getReferenceById(userId);
        UserWordProgress current = progress.findByUserIdAndWordId(userId, request.wordId())
                .orElseGet(() -> new UserWordProgress(user, word));
        Schedule schedule = schedule(current, request.rating(), requireSettings(userId).getReminderTime());
        current.record(request.rating(), schedule.intervalDays(), schedule.mastery(), schedule.nextReviewAt());
        progress.save(current);

        ReviewAnswer answer = answers.save(new ReviewAnswer(user, session, word, request.rating(), request.responseTimeMs(),
                schedule.intervalDays(), schedule.mastery(), schedule.nextReviewAt()));
        session.markOneCompleted();
        return toAnswer(answer);
    }

    private StudyDtos.QueueResponse toQueue(StudySession session, long userId) {
        Map<Long, UserWordProgress> values = progress.findByUserIdAndWordIdIn(userId,
                        session.getWords().stream().map(Word::getId).toList()).stream()
                .collect(Collectors.toMap(item -> item.getWord().getId(), Function.identity()));
        return new StudyDtos.QueueResponse(session.getId(), session.getMode(), session.getTotal(), session.getCompleted(),
                session.getWords().stream().map(word -> wordService.toDetail(word, values.get(word.getId()))).toList());
    }

    private Schedule schedule(UserWordProgress current, RecallRating rating, LocalTime reminderTime) {
        int previous = current.getIntervalDays();
        int days;
        MasteryLevel mastery;
        switch (rating) {
            case AGAIN -> { days = 0; mastery = MasteryLevel.LEARNING; }
            case HARD -> { days = Math.max(1, Math.round(previous * 1.2f)); mastery = MasteryLevel.LEARNING; }
            case GOOD -> { days = previous <= 0 ? 4 : Math.max(previous + 1, Math.round(previous * 2.5f)); mastery = MasteryLevel.FAMILIAR; }
            case EASY -> { days = previous <= 0 ? 12 : Math.max(previous + 1, Math.round(previous * 3.5f)); mastery = MasteryLevel.MASTERED; }
            default -> throw new IllegalArgumentException("Unsupported rating");
        }
        Instant next = days == 0
                ? Instant.now().plus(10, java.time.temporal.ChronoUnit.MINUTES)
                : LocalDate.now(DateTimes.ZONE).plusDays(days).atTime(reminderTime).atZone(DateTimes.ZONE).toInstant();
        return new Schedule(days, mastery, next);
    }

    private StudyDtos.AnswerResponse toAnswer(ReviewAnswer answer) {
        return new StudyDtos.AnswerResponse(DateTimes.offset(answer.getNextReviewAt()),
                answer.getIntervalDays(), answer.getMastery());
    }

    private UserSettings requireSettings(long userId) {
        return settings.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User settings missing for " + userId));
    }

    private WordLevel preferredLevel(PreferredLevel value) {
        return value == PreferredLevel.BOTH ? null : WordLevel.valueOf(value.name());
    }

    private record Schedule(int intervalDays, MasteryLevel mastery, Instant nextReviewAt) {}
}
