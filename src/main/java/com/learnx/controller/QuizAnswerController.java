package com.learnx.controller;

import com.learnx.dto.QuizAnswerDto;
import com.learnx.entity.QuizAnswer;
import com.learnx.response.Response;
import com.learnx.service.QuestionService;
import com.learnx.service.QuizAnswerService;
import com.learnx.service.QuizSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quiz-answers")
@RequiredArgsConstructor
public class QuizAnswerController {

    private final QuizAnswerService quizAnswerService;
    private final QuizSubmissionService quizSubmissionService;
    private final QuestionService questionService;

    @GetMapping("")
    public Response getAllQuizAnswer() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz answer successfully!").data(quizAnswerService.getAllQuizAnswers()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{quizAnswerId}")
    public Response getQuizAnswerById(@PathVariable("quizAnswerId") Long quizAnswerId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get quiz answer with id " + quizAnswerId + " successfully!").data(quizAnswerService.getQuizAnswerById(quizAnswerId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createQuizAnswer(@RequestBody QuizAnswerDto quizAnswerDto) {
        try {
            QuizAnswer quizAnswer = QuizAnswer.builder()
                    .answer(quizAnswerDto.getAnswer())
                    .quizSubmission(quizSubmissionService.getQuizSubmissionById(quizAnswerDto.getQuizSubmissionId()).get())
                    .quizQuestion(questionService.getQuestionById(quizAnswerDto.getQuizQuestionId()).get())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create quiz answer successfully!").data(quizAnswerService.saveQuizAnswer(quizAnswer)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{quizAnswerId}")
    public Response editQuizAnswer(@PathVariable("quizAnswerId") Long quizAnswerId, @RequestBody QuizAnswerDto quizAnswerDto) {
        try {
            Optional<QuizAnswer> quizAnswerOptional = quizAnswerService.getQuizAnswerById(quizAnswerId);
            QuizAnswer quizAnswer = quizAnswerOptional.get();
            quizAnswer = convertQuizAnswerDTO(quizAnswerDto, quizAnswerOptional);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit quiz answer with id " + quizAnswerId + " successfully!").data(quizAnswerService.saveQuizAnswer(quizAnswer)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{quizAnswerId}")
    public Response deleteQuizAnswer(@PathVariable("quizAnswerId") Long quizAnswerId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete quiz answer with id " + quizAnswerId + " successfully!").data(quizAnswerService.deleteQuizAnswer(quizAnswerId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private QuizAnswer convertQuizAnswerDTO(QuizAnswerDto quizAnswerDto, Optional<QuizAnswer> quizAnswerOptional) {
        QuizAnswer quizAnswer = quizAnswerOptional.get();
        if (quizAnswerDto.getAnswer() != null) quizAnswer.setAnswer(quizAnswerDto.getAnswer());
        if (quizAnswerDto.getQuizSubmissionId() != null) quizAnswer.setQuizSubmission(quizSubmissionService.getQuizSubmissionById(quizAnswerDto.getQuizSubmissionId()).get());
        if (quizAnswerDto.getQuizQuestionId() != null) quizAnswer.setQuizQuestion(questionService.getQuestionById(quizAnswerDto.getQuizQuestionId()).get());
        return quizAnswer;
    }

}
