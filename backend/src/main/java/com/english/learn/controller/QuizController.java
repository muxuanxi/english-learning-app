package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.model.Quiz;
import com.english.learn.model.User;
import com.english.learn.model.UserQuizRecord;
import com.english.learn.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<ApiResult<List<Quiz>>> getAllQuizzes(
            @RequestParam(required = false) String type) {
        List<Quiz> quizzes;
        if (type != null) {
            quizzes = quizService.getQuizzesByType(Quiz.QuizType.valueOf(type.toUpperCase()));
        } else {
            quizzes = quizService.getAllQuizzes();
        }
        return ResponseEntity.ok(ApiResult.success(quizzes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<Quiz>> getQuiz(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizWithQuestions(id);
        if (quiz == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ApiResult.success(quiz));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResult<UserQuizRecord>> submitQuiz(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        @SuppressWarnings("unchecked")
        Map<String, String> answers = (Map<String, String>) body.get("answers");
        int timeSpent = (int) body.getOrDefault("timeSpent", 0);

        UserQuizRecord record;
        if (user != null) {
            record = quizService.submitQuiz(user, id, answers, timeSpent);
        } else {
            record = quizService.scoreQuizAnonymous(id, answers, timeSpent);
        }
        return ResponseEntity.ok(ApiResult.success("提交成功", record));
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResult<List<UserQuizRecord>>> getRecords(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(ApiResult.success(quizService.getUserRecords(user.getId())));
    }
}
