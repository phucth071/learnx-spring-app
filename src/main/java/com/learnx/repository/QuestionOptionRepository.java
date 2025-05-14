package com.learnx.repository;

import com.learnx.entity.QuestionOption;
import com.learnx.entity.embeddedId.QuestionOptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, QuestionOptionId> {
    void deleteAllByQuestionId(Long questionId);
    Optional<QuestionOption> findById(QuestionOptionId id);
}
