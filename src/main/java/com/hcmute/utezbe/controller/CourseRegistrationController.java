package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.dto.CourseRegistrationDto;
import com.hcmute.utezbe.entity.CourseRegistration;
import com.hcmute.utezbe.request.ListEmailRequest;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CourseRegistrationService;
import com.hcmute.utezbe.service.CourseService;
import com.hcmute.utezbe.service.UserService;
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
    public Response getAllCourseRegistration() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course registration successfully!").data(courseRegistrationService.getAllCourseRegistrations()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/register/{courseId}/list-email")
    public Response registerCourse(@PathVariable("courseId") Long courseId, @RequestBody ListEmailRequest req) {
        List<CourseRegistration> courseRegistrations = courseRegistrationService.registerCourse(courseId, req.getEmails());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Register course successfully!").data(courseRegistrations).build();
    }

    @GetMapping("/pageable")
    public Response getAllCourseRegistration(Pageable pageable) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course registration successfully!").data(courseRegistrationService.getAllCourseRegistrations(pageable)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createRegistration(CourseRegistrationDto dto) {
        try {
            Long studentId = AuthService.getCurrentUser().getId();
            CourseRegistration courseRegistration = CourseRegistration.builder()
                    .course(courseService.getCourseById(dto.getCourseId()).get())
                    .email(userService.getUserById(studentId).getEmail())
                    .build();
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Create course registration successfully!").data(courseRegistrationService.save(courseRegistration)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/student/{studentId}")
    public Response getCourseRegistrationByStudentId(@PathVariable("studentId") Long studentId, Pageable pageable) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course registration by student id successfully!").data(courseRegistrationService.getCourseRegistrationsByStudentId(studentId, pageable)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/course/{courseId}")
    public Response getCourseRegistrationByCourseId(@PathVariable("courseId") Long courseId, Pageable pageable) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course registration by student id successfully!").data(courseRegistrationService.getCourseRegistrationsByCourseId(courseId, pageable)).build();
        } catch (Exception e) {
            throw e;
        }
    }


}
