package com.learnx.service;

import com.learnx.entity.Question;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository quizQuestionRepository;

    public Optional<Question> getQuestionById(Long id) {
        Optional<Question> question = quizQuestionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ResourceNotFoundException("Question with id " + id + " not found!");
        }
        return question;
    }

    public List<Question> getAllQuestions() {
        return quizQuestionRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Question saveQuestion(Question quizQuestion) {
        return quizQuestionRepository.save(quizQuestion);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Question deleteQuestion(Long id) {
        Optional<Question> quizQuestion = quizQuestionRepository.findById(id);
        quizQuestion.ifPresent(quizQuestionRepository::delete);
        return quizQuestion.orElse(null);
    }

}
