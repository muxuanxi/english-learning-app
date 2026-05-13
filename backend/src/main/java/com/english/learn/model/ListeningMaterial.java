package com.english.learn.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listening_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeningMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "audio_url", nullable = false, length = 500)
    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "subtitles_json", columnDefinition = "JSON")
    private String subtitlesJson;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 2;

    @Column(length = 100)
    private String category;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
