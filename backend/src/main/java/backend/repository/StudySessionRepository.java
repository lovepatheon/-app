package backend.repository;

import backend.domain.SessionStatus;
import backend.domain.StudyMode;
import backend.domain.StudySession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudySessionRepository extends JpaRepository<StudySession, String> {
    @EntityGraph(attributePaths = "words")
    Optional<StudySession> findFirstByUserIdAndModeAndStatusOrderByCreatedAtDesc(
            Long userId, StudyMode mode, SessionStatus status);

    @EntityGraph(attributePaths = "words")
    Optional<StudySession> findByIdAndUserId(String id, Long userId);
}
