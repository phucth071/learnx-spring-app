package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.QuestionDto;
import com.hcmute.utezbe.entity.Question;
import com.hcmute.utezbe.entity.Quiz;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.QuestionService;
import com.hcmute.utezbe.service.QuizService;
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
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all quiz question failed!").data(null).build();
        }
    }

    @GetMapping("/{questionId}")
    public Response getQuestionById(@PathVariable("questionId") Long questionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get question with id " + questionId + " successfully!").data(questionService.getQuestionById(questionId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get question with id " + questionId + " failed!").data(null).build();
        }
    }

    @PostMapping("")
    public Response createQuestion(@RequestBody QuestionDto questionDto) {
        try {
            Optional<Quiz> optionalQuiz = quizService.findById(questionDto.getQuizId(), questionDto.getModuleId());
            if (optionalQuiz.isPresent()) {
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
            } else {
                return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Quiz not found!").data(null).build();
            }
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create question failed!").data(null).build();
        }
    }

    @PatchMapping("/{questionId}")
    public Response editQuestion(@PathVariable("questionId") Long questionId, @RequestBody QuestionDto questionDto) {
        try {
            Optional<Question> questionOptional = questionService.getQuestionById(questionId);
            if (!questionOptional.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Question with id " + questionId + " not found!").data(null).build();
            }
            Question question = questionOptional.get();
            if (question != null) {
                question = convertQuestionDTO(questionDto, questionOptional);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question with id " + questionId + " successfully!").data(questionService.saveQuestion(question)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Question with id " + questionId + " not found!").data(null).build();
            }
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit question with id " + questionId + " failed!").data(null).build();
        }
    }

    @DeleteMapping("/{questionId}")
    public Response deleteQuestion(@PathVariable("questionId") Long questionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete question with id " + questionId + " successfully!").data(questionService.deleteQuestion(questionId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete question with id " + questionId + " failed!").data(null).build();
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
