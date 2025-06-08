package com.learnx.controller;

import com.learnx.auth.AuthService;
import com.learnx.dto.QuizDto;
import com.learnx.entity.Module;
import com.learnx.entity.Question;
import com.learnx.entity.Quiz;
import com.learnx.entity.enumClass.Role;
import com.learnx.entity.views.QuestionAnswerViews;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.request.CreateQuizRequest;
import com.learnx.response.Response;
import com.learnx.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuizSessionService quizSessionService;
    private final ModuleService moduleService;
    private final QuestionService questionService;
    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;

    @GetMapping("")
    public Response<?> getAllQuiz() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz successfully!").data(quizService.findAll()).build();
    }

    @GetMapping("/{quizId}")
    public Response<?> getQuizById(@PathVariable("quizId") Long quizId) {
        Quiz quiz = quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz with id " + quizId + " not found!"));
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get quiz with id " + quizId + " successfully!").data(quiz).build();
    }

    @GetMapping("/{quizId}/submissions")
    public Response<?> getQuizSubmissions(@PathVariable("quizId") Long quizId) {

        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Get quiz submissions successfully!")
                .data(quizSubmissionService.getQuizSubmissionsByQuizId(quizId))
                .build();
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
                .attemptAllowed(quizDto.getAttemptAllowed())
                .description(quizDto.getDescription())
                .isShuffled(quizDto.isShuffled())
                .status(true)
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create quiz successfully!").data(quizService.saveQuiz(quiz)).build();
    }

    @PatchMapping("/{quizId}")
    public Response<?> editQuiz(@PathVariable("quizId") Long quizId, @RequestBody CreateQuizRequest req) throws ParseException {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit quiz successfully!").data(quizService.updateQuiz(quizId, req)).build();
    }

    @GetMapping("/{quizId}/attemptAllowed")
    public Response<?> getQuizAttemptAllowed(@PathVariable("quizId") Long quizId) {
        Quiz quiz = quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz with id " + quizId + " not found!"));
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Get quiz attempt allowed successfully!")
                .data(quiz.getAttemptAllowed())
                .build();
    }

    @GetMapping("/{quizId}/session")
    public Response<?> startQuizSession(@PathVariable("quizId") Long quizId) {
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Start quiz session successfully!")
                .data(quizSessionService.getSession(quizId))
                .build();
    }

    @GetMapping("/{quizId}/questions")
    public MappingJacksonValue getQuizQuestions(@PathVariable("quizId") Long quizId) {
        quizService.findById(quizId).orElseThrow(() -> new ResourceNotFoundException("Quiz with id " + quizId + " not found!"));
        String role = AuthService.getCurrentUser().getRole().toString();
        Class<?> jsonView = Role.TEACHER.name().equalsIgnoreCase(role) ?
                QuestionAnswerViews.Teacher.class : QuestionAnswerViews.Student.class;

        List<Question> questions = quizService.findById(quizId)
                .map(quiz -> {
                    if (quiz.isShuffled()) {
                        return questionService.getQuestionsByQuizId(quizId).stream()
                                .sorted((q1, q2) -> (int) (Math.random() * 2 - 1))
                                .toList();
                    } else {
                        return questionService.getQuestionsByQuizId(quizId);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Quiz with id " + quizId + " not found!"));

        // Create the Response object first
        Response<?> response = Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Get quiz questions successfully!")
                .data(questions)
                .build();

        // Then wrap it with MappingJacksonValue
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(response);
        mappingJacksonValue.setSerializationView(jsonView);

        // Return the MappingJacksonValue directly
        return mappingJacksonValue;
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
