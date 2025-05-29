package com.learnx.repository;

import com.learnx.entity.QuizSession;
import com.learnx.entity.QuizSubmission;
import com.learnx.entity.enumClass.QuizSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {

    Optional<QuizSession> findByStudentIdAndQuizIdAndStatus(
            Long studentId, Long quizId, QuizSessionStatus status);

    Optional<QuizSession> findByStudentIdAndQuizId(
            Long studentId, Long quizId);

    List<QuizSession> findAllByStudentIdAndQuizIdAndStatus(Long studentId, Long quizId, QuizSessionStatus status);

    List<QuizSession> findByStatusAndEndTimeBefore(
            QuizSessionStatus status, LocalDateTime dateTime);

}