package com.learnx.service;

import com.learnx.entity.QuizAnswer;
import com.learnx.entity.QuizSubmission;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuizAnswerRepository;
import com.learnx.repository.QuizSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    public Optional<QuizSubmission> getQuizSubmissionById(Long Id) {
        Optional<QuizSubmission> quizSubmission = quizSubmissionRepository.findById(Id);
        if (quizSubmission.isEmpty()) {
            throw new ResourceNotFoundException("QuizSubmission with id " + Id + " not found!");
        }
        return quizSubmission;
    }

    public List<QuizSubmission> getAllQuizSubmissions() {
        return quizSubmissionRepository.findAll();
    }

    public QuizSubmission saveQuizSubmission(QuizSubmission quizSubmission) {
        return quizSubmissionRepository.save(quizSubmission);
    }

    public QuizSubmission deleteQuizSubmission(Long Id) {
        Optional<QuizSubmission> quizSubmission = quizSubmissionRepository.findById(Id);
        quizSubmission.ifPresent(qS -> {
            List<QuizAnswer> quizAnswers = quizAnswerRepository.findAllByQuizSubmissionId(qS.getId());
            quizAnswerRepository.deleteAll(quizAnswers);
            quizSubmissionRepository.delete(qS);
        });
        return quizSubmission.orElse(null);
    }

}
