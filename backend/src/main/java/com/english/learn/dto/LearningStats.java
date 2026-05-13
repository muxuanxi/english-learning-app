package com.english.learn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LearningStats {
    private long totalWordsLearned;
    private long masteredWords;
    private long totalQuizTaken;
    private double avgQuizScore;
    private long consecutiveDays;
    private long totalStudyMinutes;
}
