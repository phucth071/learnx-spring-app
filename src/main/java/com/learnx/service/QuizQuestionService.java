package com.learnx.service;

import com.learnx.entity.Question;
import com.learnx.entity.Quiz;
import com.learnx.entity.QuizQuestion;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuestionRepository;
import com.learnx.repository.QuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizQuestionService {
    private final QuestionRepository questionRepository;
    private final QuizService quizService;
    private final QuizQuestionRepository quizQuestionRepository;

    public List<QuizQuestion> findAllByQuiz_Id(Long quizId) {
        return quizQuestionRepository.findAllByQuiz_Id(quizId);
    }

    public QuizQuestion addExistedQuestionToQuiz(Long quizId, Long questionId) {
        Quiz quiz = quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        QuizQuestion quizQuestion = QuizQuestion.builder()
                .quiz(quiz)
                .question(question)
                .build();
        return quizQuestionRepository.save(quizQuestion);
    }

    public QuizQuestion addNewQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        QuizQuestion quizQuestion = QuizQuestion.builder()
                .quiz(quiz)
                .question(question)
                .build();
        return quizQuestionRepository.save(quizQuestion);
    }
}
