package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.model.GrammarLesson;
import com.english.learn.repository.GrammarLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
public class GrammarController {

    private final GrammarLessonRepository grammarRepo;

    @GetMapping
    public ResponseEntity<ApiResult<List<GrammarLesson>>> getAllLessons(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level) {
        List<GrammarLesson> lessons;
        if (category != null) {
            lessons = grammarRepo.findByCategoryOrderBySortOrderAsc(category);
        } else if (level != null) {
            lessons = grammarRepo.findByDifficultyLevelOrderBySortOrderAsc(level);
        } else {
            lessons = grammarRepo.findAllByOrderBySortOrderAsc();
        }
        return ResponseEntity.ok(ApiResult.success(lessons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<GrammarLesson>> getLesson(@PathVariable Long id) {
        return grammarRepo.findById(id)
                .map(lesson -> ResponseEntity.ok(ApiResult.success(lesson)))
                .orElse(ResponseEntity.notFound().build());
    }
}
