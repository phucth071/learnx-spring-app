package com.learnx.repository;

import com.learnx.entity.QuizSubmissionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionDetailRepository extends JpaRepository<QuizSubmissionDetail, Long> {

    Optional<QuizSubmissionDetail> findById(Long id);

    List<QuizSubmissionDetail> findAllByQuizSubmissionId(Long quizSubmissionId);

}
