package backend.repository;

import backend.domain.ReviewAnswer;
import backend.domain.StudyMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Long> {
    Optional<ReviewAnswer> findByUserIdAndSessionIdAndWordId(Long userId, String sessionId, Long wordId);

    @Query("""
        select a from ReviewAnswer a join fetch a.session
        where a.user.id = :userId and a.createdAt >= :from and a.createdAt < :to
        order by a.createdAt
        """)
    List<ReviewAnswer> findActivity(@Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    @Query("""
        select count(a) from ReviewAnswer a
        where a.user.id = :userId and a.session.mode = :mode
          and a.createdAt >= :from and a.createdAt < :to
        """)
    long countByModeAndPeriod(@Param("userId") Long userId, @Param("mode") StudyMode mode,
                              @Param("from") Instant from, @Param("to") Instant to);
}
