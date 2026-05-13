package com.english.learn.repository;

import com.english.learn.model.GrammarLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GrammarLessonRepository extends JpaRepository<GrammarLesson, Long> {
    List<GrammarLesson> findByCategoryOrderBySortOrderAsc(String category);
    List<GrammarLesson> findByDifficultyLevelOrderBySortOrderAsc(Integer level);
    List<GrammarLesson> findAllByOrderBySortOrderAsc();
}
