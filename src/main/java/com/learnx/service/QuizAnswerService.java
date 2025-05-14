package com.learnx.service;

import com.learnx.entity.QuizSubmissionDetail;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuizSubmissionDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizAnswerService {

    private final QuizSubmissionDetailRepository quizSubmissionDetailRepository;

    public Optional<QuizSubmissionDetail> getQuizAnswerById(Long Id) {
        Optional<QuizSubmissionDetail> quizAnswer = quizSubmissionDetailRepository.findById(Id);
        if (quizAnswer.isEmpty()) {
            throw new ResourceNotFoundException("QuizAnswer with id " + Id + " not found!");
        }
        return quizAnswer;
    }

    public List<QuizSubmissionDetail> getAllQuizAnswers() {
        return quizSubmissionDetailRepository.findAll();
    }

    public QuizSubmissionDetail saveQuizAnswer(QuizSubmissionDetail quizSubmissionDetail) {
        return quizSubmissionDetailRepository.save(quizSubmissionDetail);
    }

    public QuizSubmissionDetail deleteQuizAnswer(Long Id) {
        Optional<QuizSubmissionDetail> quizAnswer = quizSubmissionDetailRepository.findById(Id);
        quizAnswer.ifPresent(quizSubmissionDetailRepository::delete);
        return quizAnswer.orElse(null);
    }

}
