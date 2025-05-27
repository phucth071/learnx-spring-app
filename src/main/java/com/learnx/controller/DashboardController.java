package com.learnx.controller;


import com.learnx.entity.Assignment;
import com.learnx.response.AssignmentWithCourseId;
import com.learnx.response.Response;
import com.learnx.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final AssignmentService assignmentService;

    @GetMapping("/get-by-month-year")
    public Response<?> getAssignmentsByStudentIdAndEndDateMonthYear(@RequestParam("month") int month, @RequestParam("year") int year) {
        List<Assignment> assignments = assignmentService.getAllAssignmentsByStudentIdAndEndDateMonthYear(month, year);
        List<AssignmentWithCourseId> assignmentDtos = convertToListAssignmentWithCourseId(assignments);

        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Lấy dữ liệu dashboard thành công!").data(assignmentDtos).build();
    }

    private List<AssignmentWithCourseId> convertToListAssignmentWithCourseId(List<Assignment> assignments) {
        return assignments.stream().map(assignment -> AssignmentWithCourseId.builder()
                .id(assignment.getId())
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .courseId(assignment.getModule().getCourse().getId())
                .courseName(assignment.getModule().getCourse().getName())
                .build()).toList();
    }
}
