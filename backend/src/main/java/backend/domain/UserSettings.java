package backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "daily_new_words", nullable = false)
    private int dailyNewWords = 20;

    @Column(name = "daily_review_limit", nullable = false)
    private int dailyReviewLimit = 80;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_level", nullable = false, length = 10)
    private PreferredLevel preferredLevel = PreferredLevel.BOTH;

    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime = LocalTime.of(20, 30);

    @Column(name = "sound_enabled", nullable = false)
    private boolean soundEnabled = true;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserSettings() {}

    public UserSettings(User user) {
        this.user = user;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }

    public void update(int dailyNewWords, int dailyReviewLimit, PreferredLevel preferredLevel,
                       LocalTime reminderTime, boolean soundEnabled) {
        this.dailyNewWords = dailyNewWords;
        this.dailyReviewLimit = dailyReviewLimit;
        this.preferredLevel = preferredLevel;
        this.reminderTime = reminderTime;
        this.soundEnabled = soundEnabled;
    }

    public Long getUserId() { return userId; }
    public int getDailyNewWords() { return dailyNewWords; }
    public int getDailyReviewLimit() { return dailyReviewLimit; }
    public PreferredLevel getPreferredLevel() { return preferredLevel; }
    public LocalTime getReminderTime() { return reminderTime; }
    public boolean isSoundEnabled() { return soundEnabled; }
}
