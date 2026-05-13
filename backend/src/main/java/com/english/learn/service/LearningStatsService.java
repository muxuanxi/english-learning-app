package com.english.learn.service;

import com.english.learn.dto.LearningStats;
import com.english.learn.model.UserCheckin;
import com.english.learn.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LearningStatsService {

    private final UserLearningRecordRepository recordRepository;
    private final UserCheckinRepository checkinRepository;
    private final UserQuizRecordRepository quizRecordRepository;

    public LearningStats getUserStats(Long userId) {
        long totalWords = recordRepository.countByUserIdAndIsMasteredTrue(userId);
        long masteredWords = recordRepository.countByUserIdAndIsMasteredTrue(userId);
        long quizCount = quizRecordRepository.countByUserId(userId);
        Double avgScore = quizRecordRepository.getAverageScore(userId);

        // Calculate consecutive checkin days
        long consecutiveDays = calculateConsecutiveDays(userId);

        return LearningStats.builder()
                .totalWordsLearned(totalWords)
                .masteredWords(masteredWords)
                .totalQuizTaken(quizCount)
                .avgQuizScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0)
                .consecutiveDays(consecutiveDays)
                .totalStudyMinutes(0) // Placeholder
                .build();
    }

    private long calculateConsecutiveDays(Long userId) {
        LocalDate today = LocalDate.now();
        long consecutive = 0;

        for (int i = 0; i < 365; i++) {
            LocalDate date = today.minusDays(i);
            Optional<UserCheckin> checkin = checkinRepository.findByUserIdAndCheckinDate(userId, date);
            if (checkin.isPresent()) {
                consecutive++;
            } else if (i > 0) {
                break;
            }
        }
        return consecutive;
    }

    public UserCheckin doCheckin(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<UserCheckin> existing = checkinRepository.findByUserIdAndCheckinDate(userId, today);
        if (existing.isPresent()) {
            return existing.get();
        }

        UserCheckin checkin = UserCheckin.builder()
                .checkinDate(today)
                .build();
        // We need to set user reference properly - simplified for this demo
        return checkinRepository.save(checkin);
    }
}
