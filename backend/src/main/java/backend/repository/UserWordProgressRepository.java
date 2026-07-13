package backend.repository;

import backend.domain.MasteryLevel;
import backend.domain.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {
    Optional<UserWordProgress> findByUserIdAndWordId(Long userId, Long wordId);
    List<UserWordProgress> findByUserIdAndWordIdIn(Long userId, Collection<Long> wordIds);
    long countByUserId(Long userId);
    long countByUserIdAndMastery(Long userId, MasteryLevel mastery);
    long countByUserIdAndNextReviewAtLessThanEqual(Long userId, Instant now);
    List<UserWordProgress> findTop10ByUserIdAndMasteryOrderByLapseCountDescUpdatedAtAsc(Long userId, MasteryLevel mastery);

    @Query("select p.mastery, count(p) from UserWordProgress p where p.user.id = :userId group by p.mastery")
    List<Object[]> countMastery(@Param("userId") Long userId);
}
