package backend.service;

import backend.domain.ReviewAnswer;
import backend.repository.ReviewAnswerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ReviewAnswerRepository answers;

    public ActivityService(ReviewAnswerRepository answers) {
        this.answers = answers;
    }

    public int continuousDays(long userId) {
        LocalDate today = LocalDate.now(DateTimes.ZONE);
        Set<LocalDate> activeDates = answers.findActivity(userId, DateTimes.startOfDay(today.minusDays(370)),
                        DateTimes.startOfDay(today.plusDays(1))).stream()
                .map(ReviewAnswer::getCreatedAt)
                .map(instant -> instant.atZone(DateTimes.ZONE).toLocalDate())
                .collect(Collectors.toSet());
        LocalDate cursor = activeDates.contains(today) ? today : today.minusDays(1);
        int result = 0;
        while (activeDates.contains(cursor)) {
            result++;
            cursor = cursor.minusDays(1);
        }
        return result;
    }
}
