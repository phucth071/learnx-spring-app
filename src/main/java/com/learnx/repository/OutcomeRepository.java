package com.learnx.repository;

import com.learnx.entity.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OutcomeRepository extends JpaRepository<Outcome, Long> {
    Optional<Outcome> findByCode(String code);

    @Query("SELECT o FROM Outcome o JOIN o.courses c WHERE c.id = :courseId")
    List<Outcome> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT o FROM Outcome o JOIN o.courses c WHERE c.code = :courseCode")
    List<Outcome> findByCourseCode(@Param("courseCode") String courseCode);
}
