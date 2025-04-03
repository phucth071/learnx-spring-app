package com.learnx.repository;

import com.learnx.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    Optional<QuizAnswer> findById(Long id);

    List<QuizAnswer> findAllByQuizSubmissionId(Long quizSubmissionId);

}
