package com.learnx.service;

import com.learnx.entity.*;
import com.learnx.entity.Module;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.*;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ForumRepository forumRepository;
    private final TopicCommentRepository topicCommentRepository;
    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final LectureRepository lectureRepository;
    private final ResourcesRepository resourcesRepository;
    private final UserService userService;
    private final AssignmentRepository assignmentRepository;

    public Optional<Course> getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
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
        Optional<Course> course = courseRepository.findById(id);

        course.ifPresent(c -> {
            Forum forum = forumRepository.findByCourseId(c.getId()).orElse(null);
            if (forum != null) {
                List<Topic> topics = topicRepository.findAllByForumId(forum.getId(), Sort.by(Sort.Direction.DESC, "createdAt"));
                for (Topic topic : topics) {
                    List<TopicComment> topicComments = topicCommentRepository.findAllByTopicId(topic.getId(), Sort.by(Sort.Direction.DESC, "createdAt"));
                    topicCommentRepository.deleteAll(topicComments);
                    topicRepository.delete(topic);
                }
                forumRepository.delete(forum);
            }
            List<Module> modules = moduleRepository.findAllByCourseId(c.getId());
            for (Module module : modules) {
                List<Lecture> lectures = lectureRepository.findAllByModuleId(module.getId());
                lectureRepository.deleteAll(lectures);

                List<Assignment> assignments = module.getAssignments();
                assignmentRepository.deleteAll(assignments);

                List<Resources> resources = resourcesRepository.findAllByModuleId(module.getId());
                resourcesRepository.deleteAll(resources);
                moduleRepository.delete(module);
            }
            courseRegistrationRepository.deleteAllByCourseId(c.getId());
            courseRepository.delete(c);
        });
        return course.orElse(null);
    }

    public Page<Course> getCoursesByStudentId(Long studentId, Pageable pageable) {
        List<CourseRegistration> courseRegistrations = courseRegistrationRepository.findByEmail(userService.getUserById(studentId).getEmail());

        List<Long> courseIds = courseRegistrations.stream()
                .map(courseRegistration -> courseRegistration.getCourse().getId())
                .collect(Collectors.toList());

        List<Course> courses = courseRepository.findByIdIn(courseIds);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), courseRegistrations.size());
        List<Course> paginatedCourses = courses.subList(start, end);

        return new PageImpl<>(paginatedCourses, pageable, courseRegistrations.size());
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Page<Course> getCourseByTeacherId(Long teacherId, Pageable pageable) {
        log.info("Pageable: " + pageable);
        return courseRepository.findByTeacherId(teacherId, pageable);
    }
}
