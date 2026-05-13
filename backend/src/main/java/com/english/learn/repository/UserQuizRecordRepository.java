package com.english.learn.repository;

import com.english.learn.model.UserQuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserQuizRecordRepository extends JpaRepository<UserQuizRecord, Long> {
    List<UserQuizRecord> findByUserIdOrderByCompletedAtDesc(Long userId);

    @Query("SELECT AVG(r.score * 100.0 / r.totalScore) FROM UserQuizRecord r WHERE r.user.id = :userId")
    Double getAverageScore(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM UserQuizRecord r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
