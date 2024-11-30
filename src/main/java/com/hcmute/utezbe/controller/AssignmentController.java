package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.AssignmentDto;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.entity.enumClass.State;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.request.CreateAssignmentRequest;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.AssignmentService;
import com.hcmute.utezbe.service.CloudinaryService;
import com.hcmute.utezbe.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ModuleService moduleService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("")
    public ResponseEntity<?> getAllAssignment() {
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment successfully!").data(assignmentService.getAllAssignments()).build());
    }

    @GetMapping("/pageable")
    public ResponseEntity<?> getAllAssignmentsPageable(Pageable pageable) {
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment pageable successfully!").data(assignmentService.getAllAssignmentsPageable(pageable)).build());
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("assignmentId") Long assignmentId) {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId).orElse(null);
        if (assignment == null) {
            throw new ResourceNotFoundException("Assignment with id " + assignmentId + " not found!");
        }
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment with id " + assignmentId + " successfully!").data(assignmentService.getAssignmentById(assignmentId)).build());
    }

    @PostMapping(value = "", consumes = "multipart/form-data")
    public Response<?> createAssignment(@RequestPart("assignment") CreateAssignmentRequest req,
                                     @RequestPart(value = "document") @Nullable MultipartFile document) throws IOException {
        String urlDocument = document != null ? cloudinaryService.uploadRemainFileName(document) : null;
        Assignment assignment = Assignment.builder()
                .content(req.getContent())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .state(req.getState())
                .title(req.getTitle())
                .urlDocument(urlDocument)
                .module(moduleService.getModuleById(req.getModuleId()).get())
                .build();
        assignmentService.saveAssignment(assignment);
        AssignmentDto assignmentDto = AssignmentDto.builder()
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create assignment successfully!").data(assignmentDto).build();
    }

    @PatchMapping("/{assignmentId}")
    public Response<?> editAssignment(@PathVariable("assignmentId") Long assignmentId,
                                      @RequestParam("content") String content,
                                      @RequestParam("startDate") String startDate,
                                      @RequestParam("endDate") String endDate,
                                      @RequestParam("state") String state,
                                      @RequestParam("title") String title,
                                      @RequestParam("moduleId") Long moduleId,
                                      @RequestPart(value = "document", required = false) MultipartFile document) throws ParseException {
        Optional<Assignment> optionalAssignment = assignmentService.getAssignmentById(assignmentId);
        if (optionalAssignment.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Assignment not found").build();
        }
        String urlDocument = document != null ? cloudinaryService.upload(document) : null;
        Assignment assignment = optionalAssignment.get();
        if (content != null) assignment.setContent(content);
        if (startDate != null) assignment.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
        if (endDate != null) assignment.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(endDate));
        if (state != null) assignment.setState(State.valueOf(state));
        if (title != null) assignment.setTitle(title);
        if (moduleId != null) assignment.setModule(moduleService.getModuleById(moduleId).get());
        if (urlDocument != null) assignment.setUrlDocument(urlDocument);
        assignmentService.saveAssignment(assignment);
        AssignmentDto assignmentDto = AssignmentDto.builder()
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit assignment with id " + assignmentId + " successfully!").data(assignmentDto).build();
    }

    @DeleteMapping("/{assignmentId}")
    public Response<?> deleteAssignment(@PathVariable("assignmentId") Long assignmentId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete assignment with id " + assignmentId + " successfully!").data(assignmentService.deleteAssignment(assignmentId)).build();
    }

    private Assignment convertAssignmentDTO(AssignmentDto assignmentDto, Optional<Assignment> assignmentOptional) {
        Assignment assignment = assignmentOptional.get();
        if (assignmentDto.getContent() != null) assignment.setContent(assignmentDto.getContent());
        if (assignmentDto.getStartDate() != null) assignment.setStartDate(assignmentDto.getStartDate());
        if (assignmentDto.getState() != null) assignment.setState(assignmentDto.getState());
        if (assignmentDto.getEndDate() != null) assignment.setEndDate(assignmentDto.getEndDate());
        if (assignmentDto.getTitle() != null) assignment.setTitle(assignmentDto.getTitle());
        if (assignmentDto.getModuleId() != null) assignment.setModule(moduleService.getModuleById(assignmentDto.getModuleId()).get());
        return assignment;
    }

}
