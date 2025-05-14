package com.learnx.controller;

import com.learnx.dto.QuizDto;
import com.learnx.entity.Module;
import com.learnx.entity.Quiz;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.request.CreateQuizRequest;
import com.learnx.response.Response;
import com.learnx.service.ModuleService;
import com.learnx.service.QuestionService;
import com.learnx.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final ModuleService moduleService;
    private final QuestionService questionService;

    @GetMapping("")
    public Response<?> getAllQuiz() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz successfully!").data(quizService.findAll()).build();
    }

    @PostMapping("")
    public Response<?> createQuiz(@RequestBody QuizDto quizDto) throws IOException, ParseException {
        Module module = moduleService.getModuleById(quizDto.getModuleId()).orElseThrow(() -> new ResourceNotFoundException("Module with id " + quizDto.getModuleId() + " not found!"));

        Quiz quiz = Quiz.builder()
                .module(module)
                .title(quizDto.getTitle())
                .startDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(quizDto.getStartDate()))
                .endDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(quizDto.getEndDate()))
                .timeLimit(quizDto.getTimeLimit())
                .attemptAllowed(quizDto.getAttemptLimit())
                .description(quizDto.getDescription())
                .isShuffled(quizDto.isShuffled())
                .status(true)
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create quiz successfully!").data(quizService.saveQuiz(quiz)).build();
    }

    @PatchMapping("/{quizId}")
    public Response<?> editQuiz(@PathVariable("quizId") Long quizId, @RequestBody CreateQuizRequest req) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit quiz successfully!").data(quizService.updateQuiz(quizId, req)).build();
    }

    @GetMapping("/{quizId}/questions")
    public Response<?> getQuizQuestions(@PathVariable("quizId") Long quizId) {
        Optional<Quiz> quizOptional = quizService.findById(quizId);
        if (quizOptional.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Quiz with id " + quizId + " not found!").build();
        }
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get quiz questions successfully!").data(questionService.getQuestionsByQuizId(quizId)).build();
    }

    @DeleteMapping("/{quizId}")
    public Response<?> deleteQuiz(@PathVariable("quizId") Long quizId) {
        Optional<Quiz> quizOptional = quizService.findById(quizId);
        if (quizOptional.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Quiz with id " + quizId + " not found!").build();
        }
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete quiz successfully!").data(quizService.deleteQuiz(quizId)).build();
    }
}
