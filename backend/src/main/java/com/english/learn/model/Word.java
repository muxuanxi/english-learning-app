package com.english.learn.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "words")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(name = "phonetic_us", length = 100)
    private String phoneticUs;

    @Column(name = "phonetic_uk", length = 100)
    private String phoneticUk;

    @Column(name = "part_of_speech", length = 50)
    private String partOfSpeech;

    @Column(name = "definition_cn", nullable = false, columnDefinition = "TEXT")
    private String definitionCn;

    @Column(name = "definition_en", columnDefinition = "TEXT")
    private String definitionEn;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(name = "example_translation", columnDefinition = "TEXT")
    private String exampleTranslation;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 1;

    @Column(length = 100)
    private String category;

    @Column(name = "frequency_rank")
    private Integer frequencyRank;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
