package com.learnx.repository;

import com.learnx.entity.Question;
import com.learnx.entity.enumClass.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findById(Long id);
    List<Question> findAllByQuestionType(QuestionType questionType);
    Optional<Question> findByContent(String content);
    List<Question> findByOutcomeId(Long outcomeId);
    @Query("SELECT q FROM Question q WHERE q.outcome.code = :outcomeCode")
    List<Question> findByOutcomeCode(@Param("outcomeCode") String outcomeCode);
}
