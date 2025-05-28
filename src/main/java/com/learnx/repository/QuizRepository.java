package com.learnx.repository;

import com.learnx.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findById(Long id);

    @Query("SELECT q FROM Quiz q WHERE q.module.id = ?1")
    List<Quiz> findAllByModuleId(Long id);

    @Query("SELECT q FROM Quiz q JOIN q.module m JOIN m.course c JOIN c.courseRegistrations cr WHERE cr.email = ?1 AND MONTH(q.endDate) = ?2 AND YEAR(q.endDate) = ?3")
    List<Quiz> findAllByEmailAndEndDateMonthYear(String email, int month, int year);

    @Query("SELECT q FROM Quiz q JOIN q.module m JOIN m.course c WHERE c.teacher.id = ?1 AND MONTH(q.endDate) = ?2 AND YEAR(q.endDate) = ?3")
    List<Quiz> findAllByTeacherIdAndEndDateMonthYear(Long teacherId, int month, int year);
}
