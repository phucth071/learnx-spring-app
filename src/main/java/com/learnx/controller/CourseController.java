package com.learnx.controller;

import com.learnx.auth.AuthService;
import com.learnx.dto.CourseDto;
import com.learnx.dto.ModuleDto;
import com.learnx.dto.UserDto;

import com.learnx.entity.*;
import com.learnx.entity.Module;
import com.learnx.entity.enumClass.State;

import com.learnx.exception.ResourceNotFoundException;
import com.learnx.request.CourseCloneRequest;
import com.learnx.request.CreateCourseRequest;
import com.learnx.response.Response;
import com.learnx.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final ModuleService moduleService;
    private final UserService userService;
    private final OutcomeService outcomeService;

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
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get course by id successfully!").data(courseService.getCourseById(courseId).orElseThrow(
                () -> new ResourceNotFoundException("Course with id " + courseId + " not found!")
        )).build();
    }

    private CourseDto convertToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setThumbnail(course.getThumbnail());
        courseDto.setDescription(course.getDescription());
        courseDto.setState(course.getState());
        courseDto.setStartDate(course.getStartDate());
        courseDto.setCode(course.getCode());

        if (course.getCategory() != null) {
            courseDto.setCategoryId(course.getCategory().getId());
        }

        return courseDto;
    }

    @Transactional
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Response<?> createCourse(@RequestPart("courseInfo") @Valid CreateCourseRequest req,
                                 @RequestPart(value = "thumbnail", required = false) @Nullable MultipartFile thumbnail) throws ParseException {
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
                .state(State.OPEN)
                .code(req.getCode())
                .teacher(user)
                .deleted(false)
                .build();

        if (req.getOutcomes() != null && !req.getOutcomes().isEmpty()) {
            Set<Outcome> outcomes = outcomeService.createOutcomesForCourse(req.getOutcomes(), course);
            course.setOutcomes(outcomes);
        }

        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Tạo khóa học thành công!").data(courseService.saveCourse(course)).build();
    }

    @PostMapping(value = "/clone", consumes = {"multipart/form-data"})
    public Response<?> cloneCourse(@RequestPart("courseInfo") CourseCloneRequest req,
                                   @RequestPart(value = "thumbnail", required = false) @Nullable MultipartFile thumbnail) throws ParseException {
        Course clonedCourse = courseService.cloneCourse(req, thumbnail);
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Tạo khóa học thành công!").data(courseService.saveCourse(clonedCourse)).build();
    }

    @PatchMapping("/{courseId}")
    public Response<?> editCourse(@PathVariable("courseId") Long courseId,
                                  @RequestParam("name") @Nullable String name,
                                  @RequestParam("description") @Nullable String description,
                                  @RequestParam("categoryName") @Nullable String categoryName,
                                  @RequestParam("startDate") @Nullable String startDate,
                                  @RequestParam("state") @Nullable String state,
                                  @RequestParam("code") @Nullable String code,
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
        if (code != null) course.setCode(code);
        courseService.saveCourse(course);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Sửa khóa học thành công!").data(course).build();
    }

    @PatchMapping("/outcomes/{outcomeId}")
    public Response<?> editOutcome(@PathVariable("outcomeId") Long outcomeId,
                                   @RequestParam("code") @Nullable String code,
                                   @RequestParam("description") @Nullable String description) {
        Outcome outcome = outcomeService.getById(outcomeId).orElseThrow(() -> new ResourceNotFoundException("Outcome with id " + outcomeId + " not found!"));
        if (code != null) outcome.setCode(code);
        if (description != null) outcome.setDescription(description);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Sửa outcome thành công!").data(outcomeService.update(outcomeId, outcome)).build();
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
    @GetMapping("/{courseId}/outcomes")
    public Response<?> getOutcomeOfCourse(@PathVariable("courseId") Long courseId) {
        Course course = courseService.getCourseById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course with id " + courseId + " not found!"));
        Set<Outcome> outcomes = course.getOutcomes().stream()
                .sorted(Comparator.comparing(Outcome::getCode))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (outcomes.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("No outcomes found for this course!").build();
        }
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get outcomes by course id successfully!").data(outcomes).build();
    }

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

}

