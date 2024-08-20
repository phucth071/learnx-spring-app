package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.dto.CourseRegistrationDto;
import com.hcmute.utezbe.entity.CourseRegistration;
import com.hcmute.utezbe.entity.embeddedId.CourseRegistrationId;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CourseRegistrationService;
import com.hcmute.utezbe.service.CourseService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
            Long studentId = RequestContext.getUserId();
            CourseRegistration courseRegistration = CourseRegistration.builder()
                    .id(new CourseRegistrationId(studentId, dto.getCourseId()))
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
