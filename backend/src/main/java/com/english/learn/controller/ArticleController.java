package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.model.Article;
import com.english.learn.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleRepository articleRepo;

    @GetMapping
    public ResponseEntity<ApiResult<List<Article>>> getArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level) {
        List<Article> articles;
        if (category != null) {
            articles = articleRepo.findByCategory(category);
        } else if (level != null) {
            articles = articleRepo.findByDifficultyLevel(level);
        } else {
            articles = articleRepo.findAll();
        }
        return ResponseEntity.ok(ApiResult.success(articles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<Article>> getArticle(@PathVariable Long id) {
        return articleRepo.findById(id)
                .map(a -> ResponseEntity.ok(ApiResult.success(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResult<List<Article>>> searchArticles(@RequestParam String q) {
        return ResponseEntity.ok(ApiResult.success(articleRepo.fullTextSearch(q)));
    }
}
