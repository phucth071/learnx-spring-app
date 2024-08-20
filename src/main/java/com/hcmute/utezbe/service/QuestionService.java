package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Question;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
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

    public Question saveQuestion(Question quizQuestion) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return quizQuestionRepository.save(quizQuestion);
    }

    public Question deleteQuestion(Long id) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Question> quizQuestion = quizQuestionRepository.findById(id);
        quizQuestion.ifPresent(quizQuestionRepository::delete);
        return quizQuestion.orElse(null);
    }

}
