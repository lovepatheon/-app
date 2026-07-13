package backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_word_progress", uniqueConstraints = @UniqueConstraint(name = "uk_progress_user_word", columnNames = {"user_id", "word_id"}))
public class UserWordProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MasteryLevel mastery = MasteryLevel.NEW;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays;

    @Column(name = "ease_factor", nullable = false, precision = 5, scale = 2)
    private BigDecimal easeFactor = new BigDecimal("2.50");

    @Column(name = "next_review_at")
    private Instant nextReviewAt;

    @Column(name = "exposure_count", nullable = false)
    private int exposureCount;

    @Column(name = "correct_count", nullable = false)
    private int correctCount;

    @Column(name = "lapse_count", nullable = false)
    private int lapseCount;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserWordProgress() {}

    public UserWordProgress(User user, Word word) {
        this.user = user;
        this.word = word;
    }

    @PrePersist
    @PreUpdate
    void touch() { updatedAt = Instant.now(); }

    public void record(RecallRating rating, int intervalDays, MasteryLevel mastery, Instant nextReviewAt) {
        this.intervalDays = intervalDays;
        this.mastery = mastery;
        this.nextReviewAt = nextReviewAt;
        this.exposureCount++;
        if (rating == RecallRating.GOOD || rating == RecallRating.EASY) this.correctCount++;
        if (rating == RecallRating.AGAIN) this.lapseCount++;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Word getWord() { return word; }
    public MasteryLevel getMastery() { return mastery; }
    public int getIntervalDays() { return intervalDays; }
    public Instant getNextReviewAt() { return nextReviewAt; }
    public int getExposureCount() { return exposureCount; }
    public int getCorrectCount() { return correctCount; }
    public int getLapseCount() { return lapseCount; }
}
