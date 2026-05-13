package com.english.learn.repository;

import com.english.learn.model.UserLearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface UserLearningRecordRepository extends JpaRepository<UserLearningRecord, Long> {
    List<UserLearningRecord> findByUserIdAndRecordType(Long userId, UserLearningRecord.RecordType type);

    @Query("SELECT r FROM UserLearningRecord r WHERE r.user.id = :userId AND r.nextReviewAt <= :now")
    List<UserLearningRecord> findDueForReview(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    long countByUserIdAndIsMasteredTrue(Long userId);
}
