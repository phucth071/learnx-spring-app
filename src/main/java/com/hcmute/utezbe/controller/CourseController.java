package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.dto.CourseDto;
import com.hcmute.utezbe.dto.ModuleDto;
import com.hcmute.utezbe.dto.UserDto;
import com.hcmute.utezbe.entity.*;

import com.hcmute.utezbe.entity.Module;
import com.hcmute.utezbe.entity.enumClass.State;

import com.hcmute.utezbe.request.CreateCourseRequest;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
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
    private final ModuleService moduleService;
    private final UserService userService;

    private final CloudinaryService cloudinaryService;

    @GetMapping("")
    public Response<?> getAllCourse() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseDto> courseDtos = courses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all courses successfully!").data(courseDtos).build();
    }

    @GetMapping("/pageable")
    public Response<?> getAllCoursesPageable(Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all course pageable successfully!").data(courseService.getAllCoursesPageable(pageable)).build();
    }

    @GetMapping("/{courseId}")
    public Response<?> getCourseById(@PathVariable("courseId") Long courseId) {
        CourseDto course = courseService.getCourseByIdWithCache(courseId);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course by id successfully!").data(course).build();
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

    @Transactional
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Response<?> createCourse(@RequestPart("courseInfo") @Valid CreateCourseRequest req,
                                 @RequestPart("thumbnail") @Nullable MultipartFile thumbnail) throws ParseException {
        String thumbnailUrl;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Category category = categoryService.getCategoryByName(req.getCategoryName()).orElseGet(() -> categoryService.saveCategory(Category.builder().name(req.getCategoryName()).build()));
        if (thumbnail != null) {
            thumbnailUrl = cloudinaryService.upload(thumbnail);
        } else {
            thumbnailUrl = "https://res.cloudinary.com/dnarlcqth/image/upload/v1719906429/samples/landscapes/architecture-signs.jpg";
        }
        User user = AuthService.getCurrentUser();
        Course course = Course.builder()
                .category(category)
                .name(req.getName())
                .description(req.getDescription() == null ? "" : req.getDescription())
                .startDate(dateFormatter.parse(req.getStartDate()))
                .thumbnail(thumbnailUrl)
                .state(req.getState() != null ? req.getState() : State.OPEN)
                .teacher(user)
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Tạo khóa học thành công!").data(courseService.saveCourse(course)).build();
    }

    @PatchMapping("/{courseId}")
    public Response<?> editCourse(@PathVariable("courseId") Long courseId,
                                  @RequestParam("name") @Nullable String name,
                                  @RequestParam("description") @Nullable String description,
                                  @RequestParam("categoryName") @Nullable String categoryName,
                                  @RequestParam("startDate") @Nullable String startDate,
                                  @RequestParam("state") @Nullable String state,
                                  @RequestPart("thumbnail") @Nullable MultipartFile thumbnail) throws ParseException {
        Optional<Course> courseOtp = courseService.getCourseById(courseId);
        if (courseOtp.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Không tìm thấy khóa học!").build();
        }
        Course course = courseOtp.get();
        if (categoryName != null) {
            Category category = categoryService.getCategoryByName(categoryName).orElseGet(() -> categoryService.saveCategory(Category.builder().name(categoryName).build()));
            course.setCategory(category);
        }
        if (name != null) course.setName(name);
        if (description != null) course.setDescription(description);
        if (startDate != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (z)");
            course.setStartDate(dateFormatter.parse(startDate));
        }
        if (state != null) course.setState(State.valueOf(state));
        if (thumbnail != null) {
            course.setThumbnail(cloudinaryService.upload(thumbnail));
        }
        courseService.saveCourse(course);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Sửa khóa học thành công!").data(course).build();
    }

    @DeleteMapping("/{courseId}")
    public Response<?> deleteCourse(@PathVariable("courseId") Long courseId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa khóa học thành công!").data(courseService.deleteCourse(courseId)).build();
    }

//    @PostMapping("/email")
//    public Response<?>getCoursesByEmail(@RequestBody EmailRequest request, Pageable pageable) {
//        try {
//            Page<CourseRegistration> courseRegistrations = courseRegistrationService.getCoursesRegistrationsByStudentEmail(request.getEmail(), pageable);
//            List<Long> ids = courseRegistrations.stream().map(courseRegistration -> courseRegistration.getCourse().getId()).collect(Collectors.toList());
//            Page<Course> courses = courseService.getCourseByListId(ids, pageable);
//            List<CourseDto> courseDtos = courses.stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get courses by email successfully!").data(courseDtos).build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    @GetMapping("/{courseId}/teacher")
    public Response<?> getTeacherByCourseId(@PathVariable("courseId") Long courseId) {
        Course course = courseService.getCourseById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        User teacher = userService.getUserById(course.getTeacher().getId());
        UserDto userDto = UserDto.builder()
                .email(teacher.getEmail())
                .fullName(teacher.getFullName())
                .avatar(teacher.getAvatarUrl())
                .role(teacher.getRole())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get teacher by course id successfully!").data(userDto).build();
    }

    @GetMapping("/my-courses")
    public Response<?>getMyCourses(Pageable pageable) {
        Page<Course> course = courseService.getCoursesByStudentId(AuthService.getCurrentUser().getId(), pageable);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get my courses successfully!").data(course).build();
    }

    @GetMapping("/teacher/my-courses")
    public Response<?> getMyCoursesAsTeacher(Pageable pageable) {
        Page<Course> courses = courseService.getCourseByTeacherId(AuthService.getCurrentUser().getId(), pageable);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get my courses as teacher successfully!").data(courses).build();
    }

    @GetMapping("/{coursedId}/modules")
    public Response<?> getModulesByCourseId(@PathVariable("coursedId") Long id) {
        List<Module> modules = moduleService.findAllByCourseId(id);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get modules by course id successfully!").data(modules).build();

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

