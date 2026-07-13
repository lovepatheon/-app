package backend.repository;

import backend.domain.MasteryLevel;
import backend.domain.Word;
import backend.domain.WordLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    @Query("""
        select distinct w from Word w
        left join UserWordProgress p on p.word = w and p.user.id = :userId
        where (:keyword is null or lower(w.word) like lower(concat('%', :keyword, '%'))
               or w.briefDefinition like concat('%', :keyword, '%'))
          and (:level is null or :level member of w.levels)
          and (:mastery is null or p.mastery = :mastery
               or (:mastery = backend.domain.MasteryLevel.NEW and p.id is null))
        """)
    Page<Word> search(@Param("userId") Long userId,
                      @Param("keyword") String keyword,
                      @Param("level") WordLevel level,
                      @Param("mastery") MasteryLevel mastery,
                      Pageable pageable);

    @Query("""
        select w from Word w
        where not exists (select p.id from UserWordProgress p where p.user.id = :userId and p.word = w)
          and (:level is null or :level member of w.levels)
        order by w.id
        """)
    List<Word> findNewQueue(@Param("userId") Long userId, @Param("level") WordLevel level, Pageable pageable);

    @Query("""
        select p.word from UserWordProgress p
        where p.user.id = :userId and p.nextReviewAt <= :now
          and (:level is null or :level member of p.word.levels)
        order by p.nextReviewAt, p.id
        """)
    List<Word> findReviewQueue(@Param("userId") Long userId, @Param("level") WordLevel level,
                               @Param("now") Instant now, Pageable pageable);
}
