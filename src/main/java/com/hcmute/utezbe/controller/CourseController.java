package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.dto.CourseDto;
import com.hcmute.utezbe.dto.QuizDto;
import com.hcmute.utezbe.entity.Category;
import com.hcmute.utezbe.entity.Course;
import com.hcmute.utezbe.entity.Quiz;
import com.hcmute.utezbe.entity.enumClass.State;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CategoryService;
import com.hcmute.utezbe.service.CloudinaryService;
import com.hcmute.utezbe.service.CourseService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    private final CategoryService categoryService;

    private final UserService userService;

    private final CloudinaryService cloudinaryService;


    @GetMapping("")
    public Response getAllCourse() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course successfully!").data(courseService.getAllCourses()).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all course failed!").data(null).build();
        }
    }

    @GetMapping("/pageable")
    public Response getAllCoursesPageable(Pageable pageable) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course pageable successfully!").data(courseService.getAllCoursesPageable(pageable)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all course pageable failed!").data(null).build();
        }
    }

    @GetMapping("/{courseId}")
    public Response getCourseById(@PathVariable("courseId") Long courseId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course by id successfully!").data(courseService.getCourseById(courseId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get course by id failed!").data(null).build();
        }
    }

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Response createCourse(@RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("categoryId") Long categoryId,
                                 @RequestParam("startDate") String startDate,
                                 @RequestParam("state") @Nullable String state,
                                 @RequestPart("thumbnail") @Nullable MultipartFile thumbnail) {
        try {
            String thumbnailUrl;
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            if (thumbnail != null) {
                thumbnailUrl = cloudinaryService.upload(thumbnail);
            } else {
                thumbnailUrl = "https://res.cloudinary.com/dnarlcqth/image/upload/v1719906429/samples/landscapes/architecture-signs.jpg";
            }
            Course course = Course.builder()
                    .category(categoryService.getCategoryById(categoryId).get())
                    .name(name)
                    .description(description)
                    .startDate(dateFormatter.parse(startDate))
                    .thumbnail(thumbnailUrl)
                    .state(state != null ? State.valueOf(state) : State.OPEN)
                    .teacher(userService.getUserById(RequestContext.getUserId()))
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create course successfully!").data(courseService.saveCourse(course)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create course failed!").data(null).build();
        }
    }

    @PatchMapping("/{courseId}")
    public Response editCourse(@PathVariable("courseId") Long courseId, @RequestBody CourseDto courseDto) {
        try {
            Optional<Course> courseOtp = courseService.getCourseById(courseId);
            if (!courseOtp.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Course not found!").data(null).build();
            }
            Course course = courseOtp.get();
            if (course != null) {
                course = convertCourseDTO(courseDto, courseOtp);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit course successfully!").data(courseService.saveCourse(course)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Course not found!").data(null).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit course failed!").data(null).build();
        }
    }

    @DeleteMapping("/{courseId}")
    public Response deleteCourse(@PathVariable("courseId") Long courseId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete course successfully!").data(courseService.deleteCourse(courseId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete course failed!").data(null).build();
        }
    }

    private Course convertCourseDTO(CourseDto courseDto, Optional<Course> courseOtp) {
        Course course = courseOtp.get();
        if (courseDto.getName() != null) course.setName(courseDto.getName());
        if (courseDto.getStartDate() != null) course.setStartDate(courseDto.getStartDate());
        if (courseDto.getState() != null) course.setState(courseDto.getState());

        if (courseDto.getDescription() != null) course.setDescription(courseDto.getDescription());
        if (courseDto.getCategoryId() != null) course.setCategory(categoryService.getCategoryById(courseDto.getCategoryId()).get());
        return course;
    }
}

