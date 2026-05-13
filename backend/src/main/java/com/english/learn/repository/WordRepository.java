package com.english.learn.repository;

import com.english.learn.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByDifficultyLevel(Integer level);
    List<Word> findByCategory(String category);
    Page<Word> findByDifficultyLevel(Integer level, Pageable pageable);

    @Query("SELECT w FROM Word w WHERE w.word LIKE %:keyword% OR w.definitionCn LIKE %:keyword%")
    List<Word> search(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM words ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Word> findRandom(@Param("limit") int limit);

    @Query(value = "SELECT * FROM words WHERE difficulty_level = :level ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Word> findRandomByLevel(@Param("level") int level, @Param("limit") int limit);
}
