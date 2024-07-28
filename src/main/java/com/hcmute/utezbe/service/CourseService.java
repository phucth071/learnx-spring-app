package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.*;
import com.hcmute.utezbe.entity.Module;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ForumRepository forumRepository;
    private final TopicCommentRepository topicCommentRepository;
    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final LectureRepository lectureRepository;
    private final ResourcesRepository resourcesRepository;

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course saveCourse(Course course) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return courseRepository.save(course);
    }

    public Page<Course> getAllCoursesPageable(Pageable pageable) {
        return courseRepository.findAllPageable(pageable);
    }

    @Transactional
    public Course deleteCourse(Long id) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Course> course = courseRepository.findById(id);
        course.ifPresent(c -> {
            Forum forum = forumRepository.findByCourseId(c.getId());
            if (forum != null) {
                List<Topic> topics = topicRepository.findAllByForumId(forum.getId());
                for (Topic topic : topics) {
                    List<TopicComment> topicComments = topicCommentRepository.findAllByTopicId(topic.getId());
                    topicCommentRepository.deleteAll(topicComments);
                    topicRepository.delete(topic);
                }
                forumRepository.delete(forum);
            }
            List<Module> modules = moduleRepository.findAllByCourseId(c.getId());
            for (Module module : modules) {
                List<Lecture> lectures = lectureRepository.findAllByModuleId(module.getId());
                lectureRepository.deleteAll(lectures);

                List<Resources> resources = resourcesRepository.findAllByModuleId(module.getId());
                resourcesRepository.deleteAll(resources);
                moduleRepository.delete(module);
            }
            courseRegistrationRepository.deleteAllByCourseId(c.getId());
            courseRepository.delete(c);
        });
        return course.orElse(null);
    }

}
