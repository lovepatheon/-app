package backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_sessions")
public class StudySession {
    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StudyMode mode;

    @Column(nullable = false)
    private int total;

    @Column(nullable = false)
    private int completed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "study_session_words",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id"))
    @OrderColumn(name = "position")
    private List<Word> words = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected StudySession() {}

    public StudySession(String id, User user, StudyMode mode, List<Word> words) {
        this.id = id;
        this.user = user;
        this.mode = mode;
        this.words.addAll(words);
        this.total = words.size();
        this.createdAt = Instant.now();
    }

    public void markOneCompleted() {
        if (completed < total) completed++;
        if (completed >= total) {
            status = SessionStatus.COMPLETED;
            completedAt = Instant.now();
        }
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public StudyMode getMode() { return mode; }
    public int getTotal() { return total; }
    public int getCompleted() { return completed; }
    public SessionStatus getStatus() { return status; }
    public List<Word> getWords() { return words; }
    public Instant getCreatedAt() { return createdAt; }
}
