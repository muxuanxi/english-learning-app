package com.english.learn.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_learning_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLearningRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    private RecordType recordType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "is_mastered")
    @Builder.Default
    private Boolean isMastered = false;

    @Column(name = "study_count")
    @Builder.Default
    private Integer studyCount = 1;

    @Column(name = "last_study_at")
    private LocalDateTime lastStudyAt;

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;

    public enum RecordType {
        WORD, GRAMMAR, READING, LISTENING, QUIZ
    }

    @PrePersist
    protected void onCreate() { lastStudyAt = LocalDateTime.now(); }
}
