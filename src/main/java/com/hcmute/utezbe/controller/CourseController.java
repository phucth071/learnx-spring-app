package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.auth.request.EmailRequest;
import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.dto.CourseDto;
import com.hcmute.utezbe.dto.ModuleDto;
import com.hcmute.utezbe.entity.Course;
import com.hcmute.utezbe.entity.CourseRegistration;
import com.hcmute.utezbe.entity.Module;

import com.hcmute.utezbe.entity.enumClass.State;

import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CourseRegistrationService courseRegistrationService;


    private final CloudinaryService cloudinaryService;

    @GetMapping("")
    public Response getAllCourse() {
        try {
            List<Course> courses = courseService.getAllCourses();
            List<CourseDto> courseDtos = courses.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all courses successfully!").data(courseDtos).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/pageable")
    public Response getAllCoursesPageable(Pageable pageable) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course pageable successfully!").data(courseService.getAllCoursesPageable(pageable)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{courseId}")
    public Response getCourseById(@PathVariable("courseId") Long courseId) {
        try {
            Optional<Course> courseOtp = courseService.getCourseById(courseId);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course by id successfully!").data(convertToDto(courseOtp.get())).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private CourseDto convertToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setThumbnail(course.getThumbnail());
        courseDto.setDescription(course.getDescription());
        courseDto.setState(course.getState());
        courseDto.setStartDate(course.getStartDate());

        if (course.getCategory() != null) {
            courseDto.setCategoryId(course.getCategory().getId());
        }
        
        return courseDto;
    }

    private int[] convertDateToArray(Date date) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return new int[]{
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond(),
                localDateTime.getNano()
        };
    }

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Response createCourse(@RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("categoryId") Long categoryId,
                                 @RequestParam("startDate") String startDate,
                                 @RequestParam("state") @Nullable String state,
                                 @RequestPart("thumbnail") @Nullable MultipartFile thumbnail) throws ParseException {
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
            throw e;
        }
    }

    @PatchMapping("/{courseId}")
    public Response editCourse(@PathVariable("courseId") Long courseId,
                               @RequestParam("name") String name,
                               @RequestParam("description") String description,
                               @RequestParam("categoryId") Long categoryId,
                               @RequestParam("startDate") String startDate,
                               @RequestParam("state") @Nullable String state,
                               @RequestPart("thumbnail") @Nullable MultipartFile thumbnail) throws ParseException {
        try {
            Optional<Course> courseOtp = courseService.getCourseById(courseId);
            Course course = courseOtp.get();
            course.setName(name);
            course.setDescription(description);
            course.setCategory(categoryService.getCategoryById(categoryId).get());
            course.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
            course.setState(state != null ? State.valueOf(state) : State.OPEN);
            if (thumbnail != null) {
                course.setThumbnail(cloudinaryService.upload(thumbnail));
            }
            CourseDto courseDto = convertToDto(courseService.saveCourse(course));
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit course successfully!").data(courseDto).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{courseId}")
    public Response deleteCourse(@PathVariable("courseId") Long courseId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete course successfully!").data(courseService.deleteCourse(courseId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/email")
    public Response getCoursesByEmail(@RequestBody EmailRequest request, Pageable pageable) {
        try {
            Page<CourseRegistration> courseRegistrations = courseRegistrationService.getCoursesRegistrationsByStudentEmail(request.getEmail(), pageable);
            List<Long> ids = courseRegistrations.stream().map(courseRegistration -> courseRegistration.getCourse().getId()).collect(Collectors.toList());
            List<Course> courses = courseService.getCourseByListId(ids);
            List<CourseDto> courseDtos = courses.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get courses by email successfully!").data(courseDtos).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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

    private ModuleDto convertModuleToDto(Module module) {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(module.getId());
        moduleDto.setCourseId(module.getCourse().getId());
        moduleDto.setName(module.getName());
        moduleDto.setDescription(module.getDescription());
        return moduleDto;
    }

}

