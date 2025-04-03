package com.learnx.controller;

import com.learnx.dto.QuestionDto;
import com.learnx.entity.Question;
import com.learnx.entity.Quiz;
import com.learnx.response.Response;
import com.learnx.service.QuestionService;
import com.learnx.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/question-quizzes")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;

    @GetMapping("")
    public Response getAllQuestion() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz question successfully!").data(questionService.getAllQuestions()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{questionId}")
    public Response getQuestionById(@PathVariable("questionId") Long questionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get question with id " + questionId + " successfully!").data(questionService.getQuestionById(questionId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createQuestion(@RequestBody QuestionDto questionDto) {
        try {
            Optional<Quiz> optionalQuiz = quizService.findById(questionDto.getQuizId(), questionDto.getModuleId());
            Quiz quiz = optionalQuiz.get();
            Question question = Question.builder()
                    .content(questionDto.getContent())
                    .questionType(questionDto.getQuestionType())
                    .options(questionDto.getOptions())
                    .answers(questionDto.getAnswers())
                    .score(questionDto.getScore())
                    .quiz(quiz)
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create question successfully!").data(questionService.saveQuestion(question)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{questionId}")
    public Response editQuestion(@PathVariable("questionId") Long questionId, @RequestBody QuestionDto questionDto) {
        try {
            Optional<Question> questionOptional = questionService.getQuestionById(questionId);
            Question question = questionOptional.get();
            question = convertQuestionDTO(questionDto, questionOptional);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question with id " + questionId + " successfully!").data(questionService.saveQuestion(question)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{questionId}")
    public Response deleteQuestion(@PathVariable("questionId") Long questionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete question with id " + questionId + " successfully!").data(questionService.deleteQuestion(questionId)).build();
        } catch (Exception e) {
           throw e;
        }
    }

    private Question convertQuestionDTO(QuestionDto questionDto, Optional<Question> questionOptional) {
        Question question = questionOptional.get();
        if (questionDto.getContent() != null) question.setContent(questionDto.getContent());
        if (questionDto.getQuestionType() != null) question.setQuestionType(questionDto.getQuestionType());
        if (questionDto.getOptions() != null) question.setOptions(questionDto.getOptions());
        if (questionDto.getAnswers() != null) question.setAnswers(questionDto.getAnswers());
        if (questionDto.getScore() != null) question.setScore(questionDto.getScore());
        if (questionDto.getQuizId() != null && questionDto.getModuleId() != null) {
            Optional<Quiz> optionalQuiz = quizService.findById(questionDto.getQuizId(), questionDto.getModuleId());
            optionalQuiz.ifPresent(question::setQuiz);
        }
        return question;
    }

}
