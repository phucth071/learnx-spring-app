package com.learnx.service;

import com.learnx.entity.QuestionOption;
import com.learnx.entity.embeddedId.QuestionOptionId;
import com.learnx.repository.QuestionOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionOptionService {
    private final QuestionOptionRepository questionOptionRepository;

    public void deleteAllByQuestionId(Long questionId) {
        questionOptionRepository.deleteAllByQuestionId(questionId);
    }

    public QuestionOption findByQuestionId(Long questionId, String optionId) {
        QuestionOptionId questionOptionId = QuestionOptionId.builder()
                .questionId(questionId)
                .optionId(optionId)
                .build();
        return questionOptionRepository.findById(questionOptionId)
                .orElse(null);
    }

    public void delete(QuestionOption questionOption) {
        questionOptionRepository.delete(questionOption);
    }

    public void swapOptions(Long questionId, String optionIdSrc, String optionIdDest) {
        QuestionOption optionSrc = findByQuestionId(questionId, optionIdSrc);
        QuestionOption optionDest = findByQuestionId(questionId, optionIdDest);

        if (optionSrc != null && optionDest != null) {
            String tempContent = optionSrc.getContent();
            optionSrc.setContent(optionDest.getContent());
            optionDest.setContent(tempContent);

            questionOptionRepository.save(optionSrc);
            questionOptionRepository.save(optionDest);
        }
    }

    public QuestionOption save(QuestionOption questionOption) {
        return questionOptionRepository.save(questionOption);
    }
}
