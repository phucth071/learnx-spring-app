package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.AssignmentDto;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.AssignmentService;
import com.hcmute.utezbe.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ModuleService moduleService;

    @GetMapping("")
    public Response getAllAssignment() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment successfully!").data(assignmentService.getAllAssignments()).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all assignment failed!").data(null).build();
        }
    }

    @GetMapping("/pageable")
    public Response getAllAssignmentsPageable(Pageable pageable) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment pageable successfully!").data(assignmentService.getAllAssignmentsPageable(pageable)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all assignment pageable failed!").data(null).build();
        }
    }

    @GetMapping("/{assignmentId}")
    public Response getAssignmentById(@PathVariable("assignmentId") Long assignmentId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment with id " + assignmentId + " successfully!").data(assignmentService.getAssignmentById(assignmentId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get assignment with id " + assignmentId + " failed!").data(null).build();
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
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create assignment failed!").data(null).build();
        }
    }

    @PatchMapping("/{assignmentId}")
    public Response editAssignment(@PathVariable("assignmentId") Long assignmentId, @RequestBody AssignmentDto assignmentDto) {
        try {
            Optional<Assignment> assignmentOptional = assignmentService.getAssignmentById(assignmentId);
            if (!assignmentOptional.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Assignment with id " + assignmentId + " not found!").data(null).build();
            }
            Assignment assignment = assignmentOptional.get();
            if (assignment != null) {
                assignment = convertAssignmentDTO(assignmentDto, assignmentOptional);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit assignment with id " + assignmentId + " successfully!").data(assignmentService.saveAssignment(assignment)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Assignment with id " + assignmentId + " not found!").data(null).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit assignment with id " + assignmentId + " failed!").data(null).build();
        }
    }

    @DeleteMapping("/{assignmentId}")
    public Response deleteAssignment(@PathVariable("assignmentId") Long assignmentId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete assignment with id " + assignmentId + " successfully!").data(assignmentService.deleteAssignment(assignmentId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete assignment with id " + assignmentId + " failed!").data(null).build();
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
