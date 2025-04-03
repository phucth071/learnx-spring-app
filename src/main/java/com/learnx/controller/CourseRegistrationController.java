package com.learnx.controller;

import com.learnx.auth.AuthService;
import com.learnx.dto.CourseRegistrationDto;
import com.learnx.entity.CourseRegistration;
import com.learnx.request.ListEmailRequest;
import com.learnx.response.Response;
import com.learnx.service.CourseRegistrationService;
import com.learnx.service.CourseService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-registrations")
public class CourseRegistrationController {
    private final CourseRegistrationService courseRegistrationService;
    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("")
    public Response<?> getAllCourseRegistration() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course registration successfully!").data(courseRegistrationService.getAllCourseRegistrations()).build();
    }

    @PostMapping("/register/{courseId}/list-email")
    public Response<?> registerCourse(@PathVariable("courseId") Long courseId, @RequestBody ListEmailRequest req) {
        List<CourseRegistration> courseRegistrations = courseRegistrationService.registerCourse(courseId, req.getEmails());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Thêm sinh viên vào khóa học thành công!").data(courseRegistrations).build();
    }

    @PostMapping("/remove/{courseId}/list-email")
    public Response<?> removeCourse(@PathVariable("courseId") Long courseId, @RequestBody ListEmailRequest req) {
        courseRegistrationService.removeStudentsFromCourse(courseId, req.getEmails());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa sinh viên khỏi khóa học thành công!").data(null).build();
    }

    @GetMapping("/pageable")
    public Response<?> getAllCourseRegistration(Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course registration successfully!").data(courseRegistrationService.getAllCourseRegistrations(pageable)).build();
    }

    @PostMapping("")
    public Response<?> createRegistration(CourseRegistrationDto dto) {
        Long studentId = AuthService.getCurrentUser().getId();
        CourseRegistration courseRegistration = CourseRegistration.builder()
                .course(courseService.getCourseById(dto.getCourseId()).get())
                .email(userService.getUserById(studentId).getEmail())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Thêm sinh viên vào khóa học thành công!").data(courseRegistrationService.save(courseRegistration)).build();
    }

    @GetMapping("/student/{studentId}")
    public Response<?> getCourseRegistrationByStudentId(@PathVariable("studentId") Long studentId, Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course registration by student id successfully!").data(courseRegistrationService.getCourseRegistrationsByStudentId(studentId, pageable)).build();
    }

    @GetMapping("/course/{courseId}")
    public Response<?> getCourseRegistrationByCourseId(@PathVariable("courseId") Long courseId, Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course registration by student id successfully!").data(courseRegistrationService.getCourseRegistrationsByCourseId(courseId, pageable)).build();
    }


}
