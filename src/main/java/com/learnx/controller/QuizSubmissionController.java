package com.learnx.controller;

import com.learnx.dto.QuizSubmissionDto;
import com.learnx.entity.QuizSubmission;
import com.learnx.response.Response;
import com.learnx.service.QuizSubmissionService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@RequiredArgsConstructor
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;
    private final UserService userService;

    @GetMapping("")
    public Response getAllQuizSubmission() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz submission successfully!").data(quizSubmissionService.getAllQuizSubmissions()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{quizSubmissionId}")
    public Response getQuizSubmissionById(@PathVariable("quizSubmissionId") Long quizSubmissionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get quiz submission with id " + quizSubmissionId + " successfully!").data(quizSubmissionService.getQuizSubmissionById(quizSubmissionId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createQuizSubmission(@RequestBody QuizSubmissionDto quizSubmissionDto) {
        try {
            QuizSubmission quizSubmission = QuizSubmission.builder()
                    .score(quizSubmissionDto.getScore())
                    .totalTimes(quizSubmissionDto.getTotalTimes())
                    .totalCorrects(quizSubmissionDto.getTotalCorrects())
                    .student(userService.getUserById(quizSubmissionDto.getStudentId()))
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create quiz submission successfully!").data(quizSubmissionService.saveQuizSubmission(quizSubmission)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{quizSubmissionId}")
    public Response editQuizSubmission(@PathVariable("quizSubmissionId") Long quizSubmissionId, @RequestBody QuizSubmissionDto quizSubmissionDto) {
        try {
            Optional<QuizSubmission> quizSubmissionOptional = quizSubmissionService.getQuizSubmissionById(quizSubmissionId);
            QuizSubmission quizSubmission = convertQuizSubmissionDTO(quizSubmissionDto, quizSubmissionOptional);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit quiz submission with id " + quizSubmissionId + " successfully!").data(quizSubmissionService.saveQuizSubmission(quizSubmission)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{quizSubmissionId}")
    public Response deleteQuizSubmission(@PathVariable("quizSubmissionId") Long quizSubmissionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete quiz submission with id " + quizSubmissionId + " successfully!").data(quizSubmissionService.deleteQuizSubmission(quizSubmissionId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private QuizSubmission convertQuizSubmissionDTO(QuizSubmissionDto quizSubmissionDto, Optional<QuizSubmission> quizSubmissionOptional) {
        QuizSubmission quizSubmission = quizSubmissionOptional.get();
        if (quizSubmissionDto.getScore() != null) quizSubmission.setScore(quizSubmissionDto.getScore());
        if (quizSubmissionDto.getTotalTimes() > 0) quizSubmission.setTotalTimes(quizSubmissionDto.getTotalTimes());
        if (quizSubmissionDto.getTotalCorrects() > 0) quizSubmission.setTotalCorrects(quizSubmissionDto.getTotalCorrects());
        if (quizSubmissionDto.getStudentId() != null) quizSubmission.setStudent(userService.getUserById(quizSubmissionDto.getStudentId()));
        return quizSubmission;
    }

}
