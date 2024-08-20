package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.QuizAnswer;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.repository.QuizAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizAnswerService {

    private final QuizAnswerRepository quizAnswerRepository;

    public Optional<QuizAnswer> getQuizAnswerById(Long Id) {
        Optional<QuizAnswer> quizAnswer = quizAnswerRepository.findById(Id);
        if (quizAnswer.isEmpty()) {
            throw new ResourceNotFoundException("QuizAnswer with id " + Id + " not found!");
        }
        return quizAnswer;
    }

    public List<QuizAnswer> getAllQuizAnswers() {
        return quizAnswerRepository.findAll();
    }

    public QuizAnswer saveQuizAnswer(QuizAnswer quizAnswer) {
        return quizAnswerRepository.save(quizAnswer);
    }

    public QuizAnswer deleteQuizAnswer(Long Id) {
        Optional<QuizAnswer> quizAnswer = quizAnswerRepository.findById(Id);
        quizAnswer.ifPresent(quizAnswerRepository::delete);
        return quizAnswer.orElse(null);
    }

}
