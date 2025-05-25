package com.learnx.repository;

import com.learnx.entity.QuizSubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmissionAnswerRepository extends JpaRepository<QuizSubmissionAnswer, Long> {
}
