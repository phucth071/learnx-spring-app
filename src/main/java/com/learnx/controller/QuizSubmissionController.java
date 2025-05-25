package com.learnx.controller;

import com.learnx.auth.AuthService;
import com.learnx.dto.QuizSubmissionDto;
import com.learnx.entity.QuizSubmission;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.request.CreateQuizSubmissionRequest;
import com.learnx.response.Response;
import com.learnx.service.QuizSubmissionService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@RequiredArgsConstructor
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;

    @PostMapping
    public Response<?> submitQuiz(@RequestBody CreateQuizSubmissionRequest requestDTO) {
        QuizSubmission submission = quizSubmissionService.createQuizSubmission(requestDTO);
        return Response.builder()
                .message("Quiz submission has been submitted!")
                .code(HttpStatus.CREATED.value())
                .success(true)
                .data(submission)
                .build();
    }

    @GetMapping("/{id}")
    public Response<?> getQuizSubmission(@PathVariable Long id) {
        return Response.builder()
                .message("Quiz submission retrieved successfully!")
                .code(HttpStatus.OK.value())
                .success(true)
                .data(quizSubmissionService.getQuizSubmissionById(id).orElseThrow(() -> new ResourceNotFoundException("Quiz submission with id " + id + " not found!")))
                .build();
    }

    @GetMapping("/student/{studentId}/quiz/{quizId}")
    public Response<?> getStudentQuizSubmissions(
            @PathVariable Long studentId,
            @PathVariable Long quizId) {
        List<QuizSubmission> submissions = quizSubmissionService.getQuizSubmissionByQuizIdAndStudentId(studentId, quizId);
        return Response.builder()
                .message("Quiz submissions retrieved successfully!")
                .code(HttpStatus.OK.value())
                .success(true)
                .data(submissions)
                .build();
    }

    @GetMapping("/get-by-user/quizSubmission")
    public Response<?> getQuizSubmissionsByUser() {
        Long userId = AuthService.getCurrentUser().getId();
        List<QuizSubmission> submissions = quizSubmissionService.getQuizSubmissionsByStudentId(userId);

        if (submissions.isEmpty()) {
            return Response.builder()
                    .message("No quiz submissions found for user with id " + userId)
                    .code(HttpStatus.NOT_FOUND.value())
                    .success(false)
                    .build();
        }

        return Response.builder()
                .message("Quiz submissions retrieved successfully!")
                .code(HttpStatus.OK.value())
                .success(true)
                .data(submissions)
                .build();
    }

    @GetMapping("/get-by-user/quizSubmission/attemptedTake")
    public Response<?> getQuizSubmissionsByUserAttemptedTake() {
        Long userId = AuthService.getCurrentUser().getId();
        List<QuizSubmission> submissions = quizSubmissionService.getQuizSubmissionsByStudentId(userId);

        if (submissions.isEmpty()) {
            return Response.builder()
                    .message("No quiz submissions found for user with id " + userId)
                    .code(HttpStatus.NOT_FOUND.value())
                    .success(false)
                    .build();
        }

        return Response.builder()
                .message("Quiz submissions retrieved successfully!")
                .code(HttpStatus.OK.value())
                .success(true)
                .data(submissions.size())
                .build();
    }
}
