package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.AssignmentDto;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.AssignmentService;
import com.hcmute.utezbe.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ModuleService moduleService;

    @GetMapping("")
    public ResponseEntity<?> getAllAssignment() {
        try {
            return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment successfully!").data(assignmentService.getAllAssignments()).build());
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/pageable")
    public ResponseEntity<?> getAllAssignmentsPageable(Pageable pageable) {
        try {
            return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment pageable successfully!").data(assignmentService.getAllAssignmentsPageable(pageable)).build());
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("assignmentId") Long assignmentId) {
        try {
            Assignment assignment = assignmentService.getAssignmentById(assignmentId).orElse(null);
            if (assignment == null) {
                throw new ResourceNotFoundException("Assignment with id " + assignmentId + " not found!");
            }
            return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment with id " + assignmentId + " successfully!").data(assignmentService.getAssignmentById(assignmentId)).build());
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createAssignment(@RequestBody AssignmentDto assignmentDto) {
        try {
            Assignment assignment = Assignment.builder()
                    .module(moduleService.getModuleById(assignmentDto.getModuleId()).get())
                    .content(assignmentDto.getContent())
                    .startDate(assignmentDto.getStartDate())
                    .state(assignmentDto.getState())
                    .endDate(assignmentDto.getEndDate())
                    .title(assignmentDto.getTitle())
                    .urlDocument(assignmentDto.getUrlDocument())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create assignment successfully!").data(assignmentService.saveAssignment(assignment)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{assignmentId}")
    public Response editAssignment(@PathVariable("assignmentId") Long assignmentId, @RequestBody AssignmentDto assignmentDto) {
        try {
            Optional<Assignment> assignmentOptional = assignmentService.getAssignmentById(assignmentId);
            if (!assignmentOptional.isPresent()) {
                throw new ResourceNotFoundException("Assignment with id " + assignmentId + " not found!");
            }
            Assignment assignment = assignmentOptional.get();
            if (assignment != null) {
                assignment = convertAssignmentDTO(assignmentDto, assignmentOptional);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit assignment with id " + assignmentId + " successfully!").data(assignmentService.saveAssignment(assignment)).build();
            } else {
                throw new ResourceNotFoundException("Assignment with id " + assignmentId + " not found!");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{assignmentId}")
    public Response deleteAssignment(@PathVariable("assignmentId") Long assignmentId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete assignment with id " + assignmentId + " successfully!").data(assignmentService.deleteAssignment(assignmentId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private Assignment convertAssignmentDTO(AssignmentDto assignmentDto, Optional<Assignment> assignmentOptional) {
        Assignment assignment = assignmentOptional.get();
        if (assignmentDto.getContent() != null) assignment.setContent(assignmentDto.getContent());
        if (assignmentDto.getStartDate() != null) assignment.setStartDate(assignmentDto.getStartDate());
        if (assignmentDto.getState() != null) assignment.setState(assignmentDto.getState());
        if (assignmentDto.getEndDate() != null) assignment.setEndDate(assignmentDto.getEndDate());
        if (assignmentDto.getTitle() != null) assignment.setTitle(assignmentDto.getTitle());
        if (assignmentDto.getUrlDocument() != null) assignment.setUrlDocument(assignmentDto.getUrlDocument());
        if (assignmentDto.getModuleId() != null) assignment.setModule(moduleService.getModuleById(assignmentDto.getModuleId()).get());
        return assignment;
    }

}
