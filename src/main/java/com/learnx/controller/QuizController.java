package com.learnx.controller;

import com.learnx.dto.QuizDto;
import com.learnx.entity.Quiz;
import com.learnx.response.Response;
import com.learnx.service.ModuleService;
import com.learnx.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final ModuleService moduleService;

    @GetMapping("")
    public Response getAllQuiz() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz successfully!").data(quizService.findAll()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createQuiz(@RequestBody QuizDto quizDto) throws IOException {
        try {
            Quiz quiz = Quiz.builder()
                    .module(moduleService.getModuleById(quizDto.getModuleId()).get())
                    .title(quizDto.getTitle())
                    .startDate(quizDto.getStartDate())
                    .endDate(quizDto.getEndDate())
                    .timeLimit(quizDto.getTimeLimit())
                    .attemptAllowed(quizDto.getAttemptLimit())
                    .description(quizDto.getDescription())
                    .status(true)
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create quiz successfully!").data(quizService.saveQuiz(quiz)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{quizId}/{moduleId}")
    public Response editQuiz(@PathVariable("quizId") Long quizId, @PathVariable("moduleId") Long moduleId, @RequestBody QuizDto quizDto) {
        try {
            Optional<Quiz> quizOtp = quizService.findById(quizId, moduleId);
            Quiz quiz = convertQuizDTO(quizDto, quizOtp);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit quiz successfully!").data(quizService.saveQuiz(quiz)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private static Quiz convertQuizDTO(QuizDto quizDto, Optional<Quiz> quizOtp) {
        Quiz quiz = quizOtp.get();
        if (quizDto.getTitle() != null) quiz.setTitle(quizDto.getTitle());
        if (quizDto.getStartDate() != null) quiz.setStartDate(quizDto.getStartDate());
        if (quizDto.getEndDate() != null) quiz.setEndDate(quizDto.getEndDate());
        if (quizDto.getTimeLimit() != null) quiz.setTimeLimit(quizDto.getTimeLimit());
        if (quizDto.getAttemptLimit() != null) quiz.setAttemptAllowed(quizDto.getAttemptLimit());
        if (quizDto.getDescription() != null) quiz.setDescription(quizDto.getDescription());
        return quiz;
    }

}
