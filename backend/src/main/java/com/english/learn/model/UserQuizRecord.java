package com.english.learn.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_quiz_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "answers_json", columnDefinition = "TEXT")
    private String answersJson;

    @Column(name = "completed_at", updatable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() { completedAt = LocalDateTime.now(); }
}
