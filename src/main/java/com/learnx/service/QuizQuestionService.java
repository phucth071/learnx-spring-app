package com.learnx.service;

import com.learnx.entity.Question;
import com.learnx.entity.Quiz;
import com.learnx.entity.QuizQuestion;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuestionRepository;
import com.learnx.repository.QuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public QuizQuestion addExistedQuestionToQuiz(Long quizId, Long questionId) {
        Quiz quiz = quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        // Check if the question already exists in the quiz
        QuizQuestion eQuizQuestion = quizQuestionRepository.findByQuestionIdAndQuizId(questionId, quizId).orElse(null);
        if (eQuizQuestion != null) {
            return eQuizQuestion; // Return existing quiz question if it already exists
        }
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findAllByQuiz_Id(quizId);
        int nextSequence = quizQuestions.stream()
                .mapToInt(QuizQuestion::getSeq)
                .max()
                .orElse(-1) + 1;

        QuizQuestion quizQuestion = QuizQuestion.builder()
                .quiz(quiz)
                .question(question)
                .seq(nextSequence)
                .build();
        return quizQuestionRepository.save(quizQuestion);
    }

    @Transactional
    public QuizQuestion addNewQuestionToQuiz(Long quizId, Question question) {
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findAllByQuiz_Id(quizId);
        // Check if the question already exists in the quiz
        QuizQuestion eQuizQuestion = quizQuestionRepository.findByQuestionIdAndQuizId(question.getId(), quizId).orElse(null);
        if (eQuizQuestion != null) {
            return eQuizQuestion; // Return existing quiz question if it already exists
        }
        int nextSequence = quizQuestions.stream()
                .mapToInt(QuizQuestion::getSeq)
                .max()
                .orElse(-1) + 1;

        Quiz quiz = quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        QuizQuestion quizQuestion = QuizQuestion.builder()
                .quiz(quiz)
                .question(question)
                .seq(nextSequence)
                .build();
        return quizQuestionRepository.save(quizQuestion);
    }
}
