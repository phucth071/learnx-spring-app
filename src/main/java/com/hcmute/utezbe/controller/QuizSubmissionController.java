package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.QuizSubmissionDto;
import com.hcmute.utezbe.entity.QuizSubmission;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.QuizSubmissionService;
import com.hcmute.utezbe.service.UserService;
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
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all quiz submission failed!").data(null).build();
        }
    }

    @GetMapping("/{quizSubmissionId}")
    public Response getQuizSubmissionById(@PathVariable("quizSubmissionId") Long quizSubmissionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get quiz submission with id " + quizSubmissionId + " successfully!").data(quizSubmissionService.getQuizSubmissionById(quizSubmissionId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get quiz submission with id " + quizSubmissionId + " failed!").data(null).build();
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
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create quiz submission failed!").data(null).build();
        }
    }

    @PatchMapping("/{quizSubmissionId}")
    public Response editQuizSubmission(@PathVariable("quizSubmissionId") Long quizSubmissionId, @RequestBody QuizSubmissionDto quizSubmissionDto) {
        try {
            Optional<QuizSubmission> quizSubmissionOptional = quizSubmissionService.getQuizSubmissionById(quizSubmissionId);
            if (!quizSubmissionOptional.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Quiz submission with id " + quizSubmissionId + " not found!").data(null).build();
            }
            QuizSubmission quizSubmission = quizSubmissionOptional.get();
            if (quizSubmissionDto != null) {
                quizSubmission = convertQuizSubmissionDTO(quizSubmissionDto, quizSubmissionOptional);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit quiz submission with id " + quizSubmissionId + " successfully!").data(quizSubmissionService.saveQuizSubmission(quizSubmission)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Quiz submission with id " + quizSubmissionId + " not found!").data(null).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit quiz submission with id " + quizSubmissionId + " failed!").data(null).build();
        }
    }

    @DeleteMapping("/{quizSubmissionId}")
    public Response deleteQuizSubmission(@PathVariable("quizSubmissionId") Long quizSubmissionId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete quiz submission with id " + quizSubmissionId + " successfully!").data(quizSubmissionService.deleteQuizSubmission(quizSubmissionId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete quiz submission with id " + quizSubmissionId + " failed!").data(null).build();
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
