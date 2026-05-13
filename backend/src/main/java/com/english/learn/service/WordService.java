package com.english.learn.service;

import com.english.learn.model.User;
import com.english.learn.model.UserLearningRecord;
import com.english.learn.model.Word;
import com.english.learn.repository.UserLearningRecordRepository;
import com.english.learn.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final UserLearningRecordRepository recordRepository;

    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    public List<Word> getWordsByLevel(int level) {
        return wordRepository.findByDifficultyLevel(level);
    }

    public List<Word> getRandomWords(int limit) {
        return wordRepository.findRandom(limit);
    }

    public List<Word> getRandomWordsByLevel(int level, int limit) {
        return wordRepository.findRandomByLevel(level, limit);
    }

    public List<Word> searchWords(String keyword) {
        return wordRepository.search(keyword);
    }

    public void recordStudy(User user, Long wordId, boolean isMastered) {
        Optional<UserLearningRecord> existing = recordRepository
                .findByUserIdAndRecordType(user.getId(), UserLearningRecord.RecordType.WORD)
                .stream()
                .filter(r -> r.getTargetId().equals(wordId))
                .findFirst();

        UserLearningRecord record;
        if (existing.isPresent()) {
            record = existing.get();
            record.setStudyCount(record.getStudyCount() + 1);
            record.setIsMastered(isMastered || record.getIsMastered());
        } else {
            record = UserLearningRecord.builder()
                    .user(user)
                    .recordType(UserLearningRecord.RecordType.WORD)
                    .targetId(wordId)
                    .isMastered(isMastered)
                    .studyCount(1)
                    .build();
        }

        // Ebbinghaus review schedule: 1, 2, 4, 7, 15 days
        int[] intervals = {1, 2, 4, 7, 15};
        int idx = Math.min(record.getStudyCount() - 1, intervals.length - 1);
        record.setNextReviewAt(LocalDateTime.now().plusDays(intervals[idx]));
        record.setLastStudyAt(LocalDateTime.now());

        recordRepository.save(record);
    }
}
