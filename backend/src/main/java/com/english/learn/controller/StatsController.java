package com.english.learn.controller;

import com.english.learn.dto.ApiResult;
import com.english.learn.dto.LearningStats;
import com.english.learn.model.User;
import com.english.learn.model.UserCheckin;
import com.english.learn.service.LearningStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final LearningStatsService statsService;

    @GetMapping
    public ResponseEntity<ApiResult<LearningStats>> getStats(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(ApiResult.success(statsService.getUserStats(user.getId())));
    }

    @PostMapping("/checkin")
    public ResponseEntity<ApiResult<UserCheckin>> checkin(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        UserCheckin result = statsService.doCheckin(user.getId());
        return ResponseEntity.ok(ApiResult.success("打卡成功", result));
    }
}
