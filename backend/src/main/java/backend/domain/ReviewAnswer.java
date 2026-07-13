package backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "review_answers", uniqueConstraints = @UniqueConstraint(name = "uk_answer_user_session_word", columnNames = {"user_id", "session_id", "word_id"}))
public class ReviewAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private StudySession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RecallRating rating;

    @Column(name = "response_time_ms", nullable = false)
    private int responseTimeMs;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MasteryLevel mastery;

    @Column(name = "next_review_at", nullable = false)
    private Instant nextReviewAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ReviewAnswer() {}

    public ReviewAnswer(User user, StudySession session, Word word, RecallRating rating, int responseTimeMs,
                        int intervalDays, MasteryLevel mastery, Instant nextReviewAt) {
        this.user = user;
        this.session = session;
        this.word = word;
        this.rating = rating;
        this.responseTimeMs = responseTimeMs;
        this.intervalDays = intervalDays;
        this.mastery = mastery;
        this.nextReviewAt = nextReviewAt;
        this.createdAt = Instant.now();
    }

    public StudySession getSession() { return session; }
    public Word getWord() { return word; }
    public RecallRating getRating() { return rating; }
    public int getResponseTimeMs() { return responseTimeMs; }
    public int getIntervalDays() { return intervalDays; }
    public MasteryLevel getMastery() { return mastery; }
    public Instant getNextReviewAt() { return nextReviewAt; }
    public Instant getCreatedAt() { return createdAt; }
}
