package com.english.learn.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_checkins", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "checkin_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCheckin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "word_count")
    @Builder.Default
    private Integer wordCount = 0;

    @Column(name = "study_minutes")
    @Builder.Default
    private Integer studyMinutes = 0;

    @Column(name = "quiz_score_avg")
    private Double quizScoreAvg;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
