package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.dto.AssignmentDto;
import com.hcmute.utezbe.dto.AssignmentSubmissionDto;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.entity.AssignmentSubmission;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.request.AssignmentSubmissionRequest;
import com.hcmute.utezbe.request.ScoreRequest;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.AssignmentService;
import com.hcmute.utezbe.service.AssignmentSubmissionService;
import com.hcmute.utezbe.service.CloudinaryService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/assignment-submissions")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService assignmentSubmissionService;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("")
    public Response<?> getAllAssignmentSubmission() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment submission successfully!").data(assignmentSubmissionService.getAllAssignmentSubmissions()).build();
    }

    @GetMapping("/pageable")
    public Response<?> getAllAssignmentSubmissionsPageable(Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment submission pageable successfully!").data(assignmentSubmissionService.getAllAssignmentSubmissionsPageable(pageable)).build();
    }

    @GetMapping("/{assignmentId}/{studentId}")
    public Response<?> getAssignmentSubmissionById(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId) {
        Optional<AssignmentSubmission> assignmentSubmission = assignmentSubmissionService.getAssignmentSubmissionByAssignmentIdAndStudentId(assignmentId, studentId);
        if (assignmentSubmission.isPresent()) {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment submission successfully!").data(assignmentSubmission.get()).build();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping("/{assignmentId}/logged-in")
    public Response<?> getAssignmentByLoggedInUserAndAssignmentId(@PathVariable("assignmentId") Long assignmentId) {
        User currentUser = AuthService.getCurrentUser();
        Optional<AssignmentSubmission> assignmentSubmission = assignmentSubmissionService.getAssignmentSubmissionByAssignmentIdAndStudentId(assignmentId, currentUser.getId());
        if (assignmentSubmission.isPresent()) {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment submission successfully!").data(assignmentSubmission.get()).build();
        } else {
            throw new ResourceNotFoundException();
        }
    }


    @Transactional
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> createAssignmentSubmission(
            @RequestPart("assignment") AssignmentSubmissionRequest req,
            @RequestPart("document") @Nullable MultipartFile file) throws IOException {

        String fileUrl = null;
        if (file != null) {
            fileUrl = cloudinaryService.uploadRemainFileName(file);
        }

        User currentUser = AuthService.getCurrentUser();
        Assignment assignment = assignmentService.getAssignmentById(req.getAssignmentId()).orElseThrow(ResourceNotFoundException::new);

        AssignmentSubmission assignmentSubmission = AssignmentSubmission.builder()
                .textSubmission(req.getTextSubmission())
                .fileSubmissionUrl(fileUrl)
                .assignment(assignment)
                .student(currentUser)
                .build();
        assignmentSubmission = assignmentSubmissionService.saveAssignmentSubmission(assignmentSubmission);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Nộp bài thành công!").data(assignmentSubmission).build();
    }

    @Transactional
    @PatchMapping("/{assignmentId}")
    public Response<?> updateAssignmentSubmisson(
            @PathVariable("assignmentId") Long assignmentId,
            @RequestPart("document") @Nullable MultipartFile file) throws IOException {

        String fileUrl = null;
        if (file != null) {
            fileUrl = cloudinaryService.uploadRemainFileName(file);
        }

        AssignmentSubmission assignmentSubmission =
                assignmentSubmissionService.getAssignmentSubmissionByAssignmentIdAndStudentId(assignmentId, AuthService.getCurrentUser().getId()).orElseThrow(ResourceNotFoundException::new);

        assignmentSubmission.setFileSubmissionUrl(fileUrl);

        assignmentSubmission = assignmentSubmissionService.saveAssignmentSubmission(assignmentSubmission);
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Chỉnh sửa thành công!").data(assignmentSubmission).build();
    }

    @PatchMapping("/{assignmentId}/{studentId}")
    public Response<?> editAssignmentSubmission(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId, @RequestBody AssignmentSubmissionDto assignmentSubmissionDto) {
        Optional<AssignmentSubmission> assignmentSubmissionOpt = assignmentSubmissionService.getAssignmentSubmissionByAssignmentIdAndStudentId(assignmentId, studentId);
        if (assignmentSubmissionOpt.isPresent()) {
            AssignmentSubmission existingSubmission = assignmentSubmissionOpt.get();
            AssignmentSubmission updatedSubmission = convertAssignmentSubmissionDTO(assignmentSubmissionDto, existingSubmission);
            AssignmentSubmission savedSubmission = assignmentSubmissionService.updateAssignmentSubmission(existingSubmission, updatedSubmission);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Chỉnh sửa thành công!").data(savedSubmission).build();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @PostMapping("/{assignmentId}/{studentId}/score")
    public Response<?> updateScore(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId, @RequestBody ScoreRequest req) {
        Optional<AssignmentSubmission> assignmentSubmissionOpt = assignmentSubmissionService.getAssignmentSubmissionByAssignmentIdAndStudentId(assignmentId, studentId);
        if (assignmentSubmissionOpt.isPresent()) {
            AssignmentSubmission existingSubmission = assignmentSubmissionOpt.get();
            existingSubmission.setScore(req.getScore());
            AssignmentSubmission savedSubmission = assignmentSubmissionService.saveAssignmentSubmission(existingSubmission);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Chỉnh sửa thành công!").data(savedSubmission).build();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @GetMapping("/course/{courseId}")
    public Response<?> getAssignmentSubmissionByCourseId(@PathVariable("courseId") Long courseId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment submission by course id successfully!").data(assignmentSubmissionService.getAllAssignmentSubmissionsByCourseId(courseId)).build();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @GetMapping("/assignment/{assignmentId}")
    public Response<?> getAssignmentSubmissionByAssignmentId(@PathVariable("assignmentId") Long assignmentId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment submission by assignment id successfully!").data(assignmentSubmissionService.getAllByAssignmentId(assignmentId)).build();
    }

    @DeleteMapping("/{assignmentId}/{studentId}")
    public Response<?> deleteAssignmentSubmission(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId) {
        assignmentSubmissionService.deleteAssignmentSubmission(assignmentId, studentId);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa thành công!").data(null).build();
    }

    private AssignmentSubmission convertAssignmentSubmissionDTO(AssignmentSubmissionDto assignmentSubmissionDto, AssignmentSubmission existingSubmission) {
        if (assignmentSubmissionDto.getScore() != null) existingSubmission.setScore(assignmentSubmissionDto.getScore());
        if (assignmentSubmissionDto.getTextSubmission() != null) existingSubmission.setTextSubmission(assignmentSubmissionDto.getTextSubmission());
        if (assignmentSubmissionDto.getFileSubmissionUrl() != null) existingSubmission.setFileSubmissionUrl(assignmentSubmissionDto.getFileSubmissionUrl());
        return existingSubmission;
    }

}
