package com.learnx.controller;

import com.learnx.dto.QuizAnswerDto;
import com.learnx.entity.QuizSubmissionDetail;
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
    public Response<?> getAllQuizAnswer() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz answer successfully!").data(quizAnswerService.getAllQuizAnswers()).build();
    }

    @GetMapping("/{quizAnswerId}")
    public Response<?> getQuizAnswerById(@PathVariable("quizAnswerId") Long quizAnswerId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get quiz answer with id " + quizAnswerId + " successfully!").data(quizAnswerService.getQuizAnswerById(quizAnswerId)).build();
    }



    @PatchMapping("/{quizAnswerId}")
    public Response<?> editQuizAnswer(@PathVariable("quizAnswerId") Long quizAnswerId, @RequestBody QuizAnswerDto quizAnswerDto) {
        // TODO: PATCH QUIZ ANSWER
        return null;
    }

    @DeleteMapping("/{quizAnswerId}")
    public Response<?> deleteQuizAnswer(@PathVariable("quizAnswerId") Long quizAnswerId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete quiz answer with id " + quizAnswerId + " successfully!").data(quizAnswerService.deleteQuizAnswer(quizAnswerId)).build();
    }

}
