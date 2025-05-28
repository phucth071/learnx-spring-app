package com.learnx.controller;


import com.learnx.auth.AuthService;
import com.learnx.entity.Assignment;
import com.learnx.entity.Quiz;
import com.learnx.entity.enumClass.Role;
import com.learnx.response.AssignmentWithCourseId;
import com.learnx.response.QuizWithCourseId;
import com.learnx.response.Response;
import com.learnx.service.AssignmentService;
import com.learnx.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final AssignmentService assignmentService;
    private final QuizService quizService;

    @GetMapping("/get-by-month-year")
    public Response<?> getAssignmentsByStudentIdAndEndDateMonthYear(@RequestParam("month") int month, @RequestParam("year") int year) {
        // Get current user role
        boolean isTeacher = Role.TEACHER.equals(AuthService.getCurrentUser().getRole());

        // Get assignments based on role
        List<Assignment> assignments = isTeacher ?
                assignmentService.getAllAssignmentsByTeacherIdAndEndDateMonthYear(month, year) :
                assignmentService.getAllAssignmentsByStudentIdAndEndDateMonthYear(month, year);
        List<AssignmentWithCourseId> assignmentDtos = convertToListAssignmentWithCourseId(assignments);

        // Get quizzes based on role
        List<Quiz> quizzes = isTeacher ?
                quizService.getAllQuizzesByTeacherIdAndEndDateMonthYear(month, year) :
                quizService.getAllQuizzesByStudentIdAndEndDateMonthYear(month, year);
        List<QuizWithCourseId> quizDtos = convertToListQuizWithCourseId(quizzes);

        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("assignments", assignmentDtos);
        dashboardData.put("quizzes", quizDtos);

        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Lấy dữ liệu dashboard thành công!")
                .data(dashboardData)
                .build();
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

    private List<QuizWithCourseId> convertToListQuizWithCourseId(List<Quiz> quizzes) {
        return quizzes.stream().map(quiz -> QuizWithCourseId.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .startDate(quiz.getStartDate())
                .endDate(quiz.getEndDate())
                .shuffled(quiz.isShuffled())
                .timeLimit(quiz.getTimeLimit())
                .moduleId(quiz.getModule().getId())
                .courseId(quiz.getModule().getCourse().getId())
                .courseName(quiz.getModule().getCourse().getName())
                .build()).toList();
    }
}
