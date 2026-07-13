package backend.config;

import backend.domain.*;
import backend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
public class DemoDataInitializer implements ApplicationRunner {
    private final UserRepository users;
    private final UserSettingsRepository settings;
    private final WordRepository words;
    private final UserWordProgressRepository progress;
    private final PasswordEncoder passwordEncoder;
    private final String demoPassword;

    public DemoDataInitializer(UserRepository users, UserSettingsRepository settings, WordRepository words,
                               UserWordProgressRepository progress, PasswordEncoder passwordEncoder,
                               @Value("${app.demo.password}") String demoPassword) {
        this.users = users;
        this.settings = settings;
        this.words = words;
        this.progress = progress;
        this.passwordEncoder = passwordEncoder;
        this.demoPassword = demoPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.existsByUsernameIgnoreCase("demo")) return;
        User user = users.save(new User("demo", "demo@example.com", passwordEncoder.encode(demoPassword), "小词同学"));
        settings.save(new UserSettings(user));
        seedProgress(user, 1, RecallRating.AGAIN, 0, MasteryLevel.LEARNING, Instant.now().minus(1, ChronoUnit.HOURS));
        seedProgress(user, 3, RecallRating.GOOD, 4, MasteryLevel.FAMILIAR, Instant.now().plus(4, ChronoUnit.DAYS));
        seedProgress(user, 4, RecallRating.EASY, 12, MasteryLevel.MASTERED, Instant.now().plus(12, ChronoUnit.DAYS));
        seedProgress(user, 5, RecallRating.HARD, 1, MasteryLevel.LEARNING, Instant.now().minus(2, ChronoUnit.HOURS));
        seedProgress(user, 7, RecallRating.GOOD, 4, MasteryLevel.FAMILIAR, Instant.now().minus(1, ChronoUnit.DAYS));
        seedProgress(user, 8, RecallRating.AGAIN, 0, MasteryLevel.LEARNING, Instant.now().minus(30, ChronoUnit.MINUTES));
    }

    private void seedProgress(User user, long wordId, RecallRating rating, int interval, MasteryLevel mastery, Instant next) {
        words.findById(wordId).ifPresent(word -> {
            UserWordProgress item = new UserWordProgress(user, word);
            item.record(rating, interval, mastery, next);
            progress.save(item);
        });
    }
}
