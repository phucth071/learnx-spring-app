package com.learnx.repository;

import com.learnx.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    Optional<QuizSubmission> findById(Long id);
    List<QuizSubmission> findAllByQuizId(Long quizId);
    List<QuizSubmission> findAllByQuizIdAndStudentId(Long quizId, Long studentId);
    List<QuizSubmission> findAllByStudentId(Long studentId);
    Optional<QuizSubmission> findByQuizIdAndStudentIdAndScoreIsNull(Long quizId, Long studentId);
}
