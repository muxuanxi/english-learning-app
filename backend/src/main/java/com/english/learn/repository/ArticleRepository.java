package com.english.learn.repository;

import com.english.learn.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(String category);
    List<Article> findByDifficultyLevel(Integer level);
    Page<Article> findByDifficultyLevel(Integer level, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.contentHtml) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Article> fullTextSearch(@Param("keyword") String keyword);
}
