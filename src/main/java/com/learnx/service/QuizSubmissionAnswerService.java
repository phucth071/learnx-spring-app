package com.learnx.service;

import com.learnx.entity.QuizSubmissionAnswer;
import com.learnx.repository.QuizSubmissionAnswerRepository;
import com.learnx.repository.QuizSubmissionDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizSubmissionAnswerService {
    private final QuizSubmissionAnswerRepository quizSubmissionAnswerRepository;

    public List<QuizSubmissionAnswer> saveAll(List<QuizSubmissionAnswer> answers) {
        return quizSubmissionAnswerRepository.saveAll(answers);
    }

    public void deleteAll(List<QuizSubmissionAnswer> answers) {
        quizSubmissionAnswerRepository.deleteAll(answers);
    }

}
