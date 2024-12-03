package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.dto.AssignmentDto;
import com.hcmute.utezbe.dto.AssignmentSubmissionDto;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.entity.AssignmentSubmission;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.request.AssignmentSubmissionRequest;
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
    public Response getAllAssignmentSubmission() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment submission successfully!").data(assignmentSubmissionService.getAllAssignmentSubmissions()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/pageable")
    public Response getAllAssignmentSubmissionsPageable(Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment submission pageable successfully!").data(assignmentSubmissionService.getAllAssignmentSubmissionsPageable(pageable)).build();
    }

    @GetMapping("/{assignmentId}/{studentId}")
    public Response getAssignmentSubmissionById(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId) {
        Optional<AssignmentSubmission> assignmentSubmission = assignmentSubmissionService.getAssignmentSubmissionById(assignmentId, studentId);
        if (assignmentSubmission.isPresent()) {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment submission successfully!").data(assignmentSubmission.get()).build();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping("/{assignmentId}/logged-in")
    public Response<?> getAssignmentByLoggedInUserAndAssignmentId(@PathVariable("assignmentId") Long assignmentId) {
        User currentUser = AuthService.getCurrentUser();
        Optional<AssignmentSubmission> assignmentSubmission = assignmentSubmissionService.getAssignmentSubmissionById(assignmentId, currentUser.getId());
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
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Create assignment submission successfully!").data(assignmentSubmission).build();
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
                assignmentSubmissionService.getAssignmentSubmissionById(assignmentId, AuthService.getCurrentUser().getId()).orElseThrow(ResourceNotFoundException::new);

        assignmentSubmission.setFileSubmissionUrl(fileUrl);

        assignmentSubmission = assignmentSubmissionService.saveAssignmentSubmission(assignmentSubmission);
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create assignment submission successfully!").data(assignmentSubmission).build();
    }

    @PatchMapping("/{assignmentId}/{studentId}")
    public Response editAssignmentSubmission(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId, @RequestBody AssignmentSubmissionDto assignmentSubmissionDto) {
        try {
            Optional<AssignmentSubmission> assignmentSubmissionOpt = assignmentSubmissionService.getAssignmentSubmissionById(assignmentId, studentId);
            if (assignmentSubmissionOpt.isPresent()) {
                AssignmentSubmission existingSubmission = assignmentSubmissionOpt.get();
                AssignmentSubmission updatedSubmission = convertAssignmentSubmissionDTO(assignmentSubmissionDto, existingSubmission);
                AssignmentSubmission savedSubmission = assignmentSubmissionService.updateAssignmentSubmission(existingSubmission, updatedSubmission);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Assignment submission updated successfully!").data(savedSubmission).build();
            } else {
                throw new ResourceNotFoundException();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{assignmentId}/{studentId}")
    public Response deleteAssignmentSubmission(@PathVariable("assignmentId") Long assignmentId, @PathVariable("studentId") Long studentId) {
        try {
            assignmentSubmissionService.deleteAssignmentSubmission(assignmentId, studentId);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Assignment submission deleted successfully!").data(null).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private AssignmentSubmission convertAssignmentSubmissionDTO(AssignmentSubmissionDto assignmentSubmissionDto, AssignmentSubmission existingSubmission) {
        if (assignmentSubmissionDto.getScore() != null) existingSubmission.setScore(assignmentSubmissionDto.getScore());
        if (assignmentSubmissionDto.getTextSubmission() != null) existingSubmission.setTextSubmission(assignmentSubmissionDto.getTextSubmission());
        if (assignmentSubmissionDto.getFileSubmissionUrl() != null) existingSubmission.setFileSubmissionUrl(assignmentSubmissionDto.getFileSubmissionUrl());
        return existingSubmission;
    }

}
