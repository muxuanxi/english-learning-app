package com.english.learn.repository;

import com.english.learn.model.UserCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserCheckinRepository extends JpaRepository<UserCheckin, Long> {
    Optional<UserCheckin> findByUserIdAndCheckinDate(Long userId, LocalDate date);

    @Query("SELECT c FROM UserCheckin c WHERE c.user.id = :userId AND c.checkinDate BETWEEN :start AND :end ORDER BY c.checkinDate")
    List<UserCheckin> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    @Query("SELECT COUNT(c) FROM UserCheckin c WHERE c.user.id = :userId AND c.checkinDate BETWEEN :start AND :end")
    long countConsecutiveDays(@Param("userId") Long userId,
                               @Param("start") LocalDate start,
                               @Param("end") LocalDate end);
}
