package com.learnx.repository;

import com.learnx.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findAllByQuiz_Id(Long quizId);
    Optional<QuizQuestion> findByQuestionIdAndQuizId(Long questionId, Long quizId);
}
