package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.dto.OutcomeDTO;
import com.learnx.entity.*;
import com.learnx.entity.Module;
import com.learnx.entity.enumClass.State;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.*;
import com.learnx.request.CourseCloneRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final UserService userService;
    private final CourseRepository courseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ForumRepository forumRepository;
    private final TopicCommentRepository topicCommentRepository;
    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final LectureRepository lectureRepository;
    private final ResourcesRepository resourcesRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final OutcomeRepository outcomeRepository;

    public Optional<Course> getCourseById(Long id) {
        Course course = courseRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (course == null) {
            throw new ResourceNotFoundException("Course with id " + id + " not found!");
        }
        return courseRepository.findById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Page<Course> getAllCoursesPageable(Pageable pageable) {
        return courseRepository.findAllPageable(pageable);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @PostAuthorize("returnObject == null or returnObject.getTeacher().getId() == principal.id")
    @Transactional
    public Course deleteCourse(Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);

        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            course.setDeleted(true);
            return courseRepository.save(course);
        }

        return null;
    }

    public Page<Course> getCoursesByStudentId(Long studentId, Pageable pageable) {
        List<CourseRegistration> courseRegistrations = courseRegistrationRepository.findByEmail(userService.getUserById(studentId).getEmail());

        List<Long> courseIds = courseRegistrations.stream()
                .map(courseRegistration -> courseRegistration.getCourse().getId())
                .collect(Collectors.toList());

        List<Course> courses = courseRepository.findByIdInAndDeletedFalse(courseIds);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), courseRegistrations.size());
        List<Course> paginatedCourses = courses.subList(start, end);

        return new PageImpl<>(paginatedCourses, pageable, courseRegistrations.size());
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Page<Course> getCourseByTeacherId(Long teacherId, Pageable pageable) {
        return courseRepository.findByTeacherIdAndDeletedFalse(teacherId, pageable);
    }

    @Transactional
    public Course cloneCourse(CourseCloneRequest req, MultipartFile thumbnail) throws ParseException {
        Course sourceCourse = courseRepository.findByIdAndDeletedFalse(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Source course not found with id: " + req.getCourseId()));

        String thumbnailUrl = "";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Category category = categoryService.getCategoryByName(req.getCategoryName()).orElseGet(() -> categoryService.saveCategory(Category.builder().name(req.getCategoryName()).build()));
        if (thumbnail != null) {
            thumbnailUrl = cloudinaryService.upload(thumbnail);
        } else {
            thumbnailUrl = "https://res.cloudinary.com/dnarlcqth/image/upload/v1719906429/samples/landscapes/architecture-signs.jpg";
        }
        User user = AuthService.getCurrentUser();

        // Process outcomes - combining source course outcomes with any new ones from the request
        Set<Outcome> courseOutcomes = processOutcomes(req.getOutcomes(), sourceCourse.getOutcomes());

        // Create new course with basic information from request
        Course newCourse = Course.builder()
                .name(req.getName() != null && !req.getName().isEmpty() ? req.getName() : sourceCourse.getName())
                .startDate(req.getStartDate() != null && !req.getStartDate().isEmpty() ? dateFormatter.parse(req.getStartDate()) : sourceCourse.getStartDate())
                .thumbnail(thumbnailUrl)
                .description(req.getDescription() != null && !req.getDescription().isEmpty() ? req.getDescription() : sourceCourse.getDescription())
                .code(req.getCode() != null && !req.getCode().isEmpty() ? req.getCode() : sourceCourse.getCode())
                .state(State.OPEN)
                .category(category)
                .teacher(user)
                .outcomes(courseOutcomes)
                .courseRegistrations(new HashSet<>())
                .deleted(false)
                .build();

        // Save the course first to get an ID
        Course savedCourse = courseRepository.save(newCourse);

        // Clone modules and their content
        List<Module> clonedModules = cloneModules(sourceCourse.getModules(), savedCourse);
        savedCourse.setModules(clonedModules);

        return savedCourse;
    }

    private Set<Outcome> processOutcomes(List<OutcomeDTO> requestOutcomes, Set<Outcome> sourceOutcomes) {
        // Create a new set to store all outcomes for the new course
        Set<Outcome> newCourseOutcomes = new HashSet<>();

        // Add all existing outcomes from the source course
        if (sourceOutcomes != null) {
            newCourseOutcomes.addAll(sourceOutcomes);
        }

        // If there are no requested outcomes, just return the source outcomes
        if (requestOutcomes == null || requestOutcomes.isEmpty()) {
            return newCourseOutcomes;
        }

        // Create a set of existing outcome codes for quick lookup
        Set<String> existingOutcomeCodes = sourceOutcomes.stream()
                .map(Outcome::getCode)
                .collect(Collectors.toSet());

        // Process each outcome from the request
        for (OutcomeDTO outcomeDTO : requestOutcomes) {
            // If this outcome code doesn't exist in the source outcomes, create and add it
            if (!existingOutcomeCodes.contains(outcomeDTO.getCode())) {
                Outcome newOutcome = Outcome.builder()
                        .code(outcomeDTO.getCode())
                        .description(outcomeDTO.getDescription())
                        .build();

                // Save the new outcome to get an ID
                newOutcome = outcomeRepository.save(newOutcome);
                newCourseOutcomes.add(newOutcome);
            }
        }

        return newCourseOutcomes;
    }

    private List<Module> cloneModules(List<Module> sourceModules, Course newCourse) {
        if (sourceModules == null || sourceModules.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceModules.stream()
                .map(sourceModule -> {
                    Module newModule = new Module();
                    newModule.setName(sourceModule.getName());
                    newModule.setDescription(sourceModule.getDescription());
                    newModule.setCourse(newCourse);

                    // Save module to get ID
                    Module savedModule = moduleRepository.save(newModule);

                    // Clone lectures
                    if (sourceModule.getLectures() != null) {
                        savedModule.setLectures(
                                cloneLectures(sourceModule.getLectures(), savedModule)
                        );
                    }

                    // Clone assignments
                    if (sourceModule.getAssignments() != null) {
                        savedModule.setAssignments(
                                cloneAssignments(sourceModule.getAssignments(), savedModule)
                        );
                    }

                    // Clone quizzes
                    if (sourceModule.getQuizzes() != null) {
                        savedModule.setQuizzes(
                                cloneQuizzes(sourceModule.getQuizzes(), savedModule)
                        );
                    }

                    return savedModule;
                })
                .collect(Collectors.toList());
    }

    private List<Lecture> cloneLectures(List<Lecture> sourceLectures, Module newModule) {
        if (sourceLectures == null || sourceLectures.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceLectures.stream()
                .map(sourceLecture -> {
                    Lecture newLecture = new Lecture();
                    newLecture.setTitle(sourceLecture.getTitle());
                    newLecture.setContent(sourceLecture.getContent());
                    newLecture.setModule(newModule);

                    // Save lecture to get ID
                    return lectureRepository.save(newLecture);
                })
                .collect(Collectors.toList());
    }

    private List<Resources> cloneResources(List<Resources> sourceResources, Module newModule) {
        if (sourceResources == null || sourceResources.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceResources.stream()
                .map(sourceResource -> {
                    Resources newResource = new Resources();
                    newResource.setTitle(sourceResource.getTitle());
                    newResource.setUrlDocument(sourceResource.getUrlDocument());
                    newResource.setModule(newModule);

                    return resourcesRepository.save(newResource);
                })
                .collect(Collectors.toList());
    }

    private List<Assignment> cloneAssignments(List<Assignment> sourceAssignments, Module newModule) {
        if (sourceAssignments == null || sourceAssignments.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceAssignments.stream()
                .map(sourceAssignment -> {
                    Assignment newAssignment = new Assignment();
                    newAssignment.setTitle(sourceAssignment.getTitle());
                    newAssignment.setContent(sourceAssignment.getContent());
                    newAssignment.setStartDate(sourceAssignment.getStartDate());
                    newAssignment.setEndDate(sourceAssignment.getEndDate());
                    newAssignment.setClosedDate(sourceAssignment.getClosedDate());
                    newAssignment.setState(sourceAssignment.getState());
                    newAssignment.setUrlDocument(sourceAssignment.getUrlDocument());
                    newAssignment.setModule(newModule);
                    return assignmentRepository.save(newAssignment);
                })
                .collect(Collectors.toList());
    }

    private List<Quiz> cloneQuizzes(List<Quiz> sourceQuizzes, Module newModule) {
        if (sourceQuizzes == null || sourceQuizzes.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceQuizzes.stream()
                .map(sourceQuiz -> {
                    Quiz newQuiz = new Quiz();
                    newQuiz.setTitle(sourceQuiz.getTitle());
                    newQuiz.setDescription(sourceQuiz.getDescription());
                    newQuiz.setTimeLimit(sourceQuiz.getTimeLimit());
                    newQuiz.setShuffled(sourceQuiz.isShuffled());
                    newQuiz.setStatus(sourceQuiz.isStatus());
                    newQuiz.setStartDate(sourceQuiz.getStartDate());
                    newQuiz.setEndDate(sourceQuiz.getEndDate());
                    newQuiz.setAttemptAllowed(sourceQuiz.getAttemptAllowed());
                    newQuiz.setModule(newModule);

                    Quiz savedQuiz = quizRepository.save(newQuiz);

                    // Reference the same QuizQuestions rather than cloning them
                    if (sourceQuiz.getQuizQuestions() != null) {
                        registerQuizQuestions(sourceQuiz.getQuizQuestions(), savedQuiz);
                    }

                    return savedQuiz;
                })
                .collect(Collectors.toList());
    }

    private void registerQuizQuestions(List<QuizQuestion> sourceQuestions, Quiz newQuiz) {
        if (sourceQuestions == null || sourceQuestions.isEmpty()) {
            return;
        }

        List<QuizQuestion> newQuizQuestions = new ArrayList<>();

        for (QuizQuestion sourceQuestion : sourceQuestions) {
            // Create a new association but reference the same Question
            QuizQuestion newQuizQuestion = new QuizQuestion();
            newQuizQuestion.setQuiz(newQuiz);
            newQuizQuestion.setQuestion(sourceQuestion.getQuestion());

            // Any other properties specific to the Quiz-Question relationship

            newQuizQuestions.add(quizQuestionRepository.save(newQuizQuestion));
        }

        // Update the quiz with the new questions
        newQuiz.setQuizQuestions(newQuizQuestions);
        quizRepository.save(newQuiz);
    }
}
