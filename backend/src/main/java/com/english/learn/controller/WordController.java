package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.model.User;
import com.english.learn.model.Word;
import com.english.learn.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    public ResponseEntity<ApiResult<List<Word>>> getWords(
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean random) {

        List<Word> words;
        if (random && level != null) {
            words = wordService.getRandomWordsByLevel(level, limit);
        } else if (random) {
            words = wordService.getRandomWords(limit);
        } else if (level != null) {
            words = wordService.getWordsByLevel(level);
        } else {
            words = wordService.getAllWords();
        }
        return ResponseEntity.ok(ApiResult.success(words));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResult<List<Word>>> searchWords(@RequestParam String q) {
        return ResponseEntity.ok(ApiResult.success(wordService.searchWords(q)));
    }

    @PostMapping("/{wordId}/study")
    public ResponseEntity<ApiResult<String>> recordStudy(
            @AuthenticationPrincipal User user,
            @PathVariable Long wordId,
            @RequestBody Map<String, Boolean> body) {
        if (user == null) return ResponseEntity.status(401).build();
        wordService.recordStudy(user, wordId, body.getOrDefault("mastered", false));
        return ResponseEntity.ok(ApiResult.success("记录成功"));
    }
}
