package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.QuizAnswer;
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
        return quizAnswerRepository.findById(Id);
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
