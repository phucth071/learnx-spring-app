package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Question;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.repository.QuestionRepository;
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
