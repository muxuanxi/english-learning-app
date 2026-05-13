package com.english.learn.service;

import com.english.learn.model.Quiz;
import com.english.learn.model.User;
import com.english.learn.model.UserQuizRecord;
import com.english.learn.repository.QuizRepository;
import com.english.learn.repository.UserQuizRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserQuizRecordRepository userQuizRecordRepository;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizzesByType(Quiz.QuizType type) {
        return quizRepository.findByQuizType(type);
    }

    public Quiz getQuizWithQuestions(Long id) {
        return quizRepository.findByIdWithQuestions(id);
    }

    public UserQuizRecord submitQuiz(User user, Long quizId, Map<String, String> answers, int timeSpentSeconds) {
        UserQuizRecord record = calculateScore(quizId, answers, timeSpentSeconds);
        record.setUser(user);
        return userQuizRecordRepository.save(record);
    }

    public UserQuizRecord scoreQuizAnonymous(Long quizId, Map<String, String> answers, int timeSpentSeconds) {
        return calculateScore(quizId, answers, timeSpentSeconds);
    }

    private UserQuizRecord calculateScore(Long quizId, Map<String, String> answers, int timeSpentSeconds) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId);
        if (quiz == null) throw new RuntimeException("测验不存在");

        int totalScore = quiz.getQuestions().stream().mapToInt(q -> q.getScore()).sum();
        int earnedScore = 0;

        for (var question : quiz.getQuestions()) {
            String userAnswer = answers.get(String.valueOf(question.getId()));
            if (userAnswer != null && userAnswer.trim().equalsIgnoreCase(question.getCorrectAnswer().trim())) {
                earnedScore += question.getScore();
            }
        }

        return UserQuizRecord.builder()
                .quizId(quizId)
                .score(earnedScore)
                .totalScore(totalScore)
                .timeSpentSeconds(timeSpentSeconds)
                .build();
    }

    public List<UserQuizRecord> getUserRecords(Long userId) {
        return userQuizRecordRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }
}
