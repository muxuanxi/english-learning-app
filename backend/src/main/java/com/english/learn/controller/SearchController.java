package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.model.Article;
import com.english.learn.model.GrammarLesson;
import com.english.learn.model.Word;
import com.english.learn.repository.ArticleRepository;
import com.english.learn.repository.GrammarLessonRepository;
import com.english.learn.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final WordRepository wordRepo;
    private final ArticleRepository articleRepo;
    private final GrammarLessonRepository grammarRepo;

    @GetMapping
    public ResponseEntity<ApiResult<Map<String, Object>>> globalSearch(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResult.success(Map.of(
                "words", List.of(),
                "articles", List.of(),
                "grammar", List.of()
            )));
        }

        String keyword = q.trim();
        List<Word> words = wordRepo.search(keyword);
        List<Article> articles = articleRepo.fullTextSearch(keyword);

        // Grammar search by title/description containing keyword
        List<GrammarLesson> grammar = grammarRepo.findAll().stream()
            .filter(g -> g.getTitle().contains(keyword) ||
                         (g.getDescription() != null && g.getDescription().contains(keyword)) ||
                         (g.getContentHtml() != null && g.getContentHtml().toLowerCase().contains(keyword.toLowerCase())))
            .limit(10)
            .toList();

        Map<String, Object> result = Map.of(
            "words", words,
            "articles", articles,
            "grammar", grammar,
            "total", words.size() + articles.size() + grammar.size()
        );

        return ResponseEntity.ok(ApiResult.success(result));
    }

    @GetMapping("/suggest")
    public ResponseEntity<ApiResult<List<String>>> suggest(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResult.success(List.of()));
        }

        String keyword = q.trim();
        Set<String> suggestions = new LinkedHashSet<>();

        // Word suggestions
        wordRepo.search(keyword).stream()
            .limit(5)
            .forEach(w -> suggestions.add(w.getWord()));

        // Article title suggestions
        articleRepo.fullTextSearch(keyword).stream()
            .limit(3)
            .forEach(a -> suggestions.add(a.getTitle()));

        // Grammar title suggestions
        grammarRepo.findAll().stream()
            .filter(g -> g.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .limit(3)
            .forEach(g -> suggestions.add(g.getTitle()));

        return ResponseEntity.ok(ApiResult.success(new ArrayList<>(suggestions)));
    }
}
