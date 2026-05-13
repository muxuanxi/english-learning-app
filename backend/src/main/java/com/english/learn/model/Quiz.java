package com.english.learn.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false)
    private QuizType quizType;

    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 2;

    @Column(name = "time_limit_minutes")
    @Builder.Default
    private Integer timeLimitMinutes = 0;

    @Column(name = "total_questions")
    @Builder.Default
    private Integer totalQuestions = 0;

    @Column(name = "pass_score")
    @Builder.Default
    private Integer passScore = 60;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<QuizQuestion> questions;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum QuizType {
        VOCABULARY, GRAMMAR, READING, LISTENING, COMPREHENSIVE
    }

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
