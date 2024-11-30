package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.dto.CourseDto;
import com.hcmute.utezbe.entity.*;
import com.hcmute.utezbe.entity.Module;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.redis.RedisService;
import com.hcmute.utezbe.redisson.RedisDistributedLocker;
import com.hcmute.utezbe.redisson.RedisDistributedService;
import com.hcmute.utezbe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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

    private final RedisDistributedService redisDistributedService;
    private final RedisService redisService;

    public Optional<Course> getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            throw new ResourceNotFoundException("Course with id " + id + " not found!");
        }
        return courseRepository.findById(id);
    }

    public CourseDto getCourseByIdWithCache(Long id) {
        log.info("Get course by id with cache: {}", id);
        CourseDto course = redisService.getObject(genCourseItemKey(id), CourseDto.class);
        if (course != null) {
            log.info("FROM CACHE EXIST {}", course);
            return course;
        }
        log.info("CACHE NO EXIST, START GET DB AND SET CACHE->, {}", id);
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("LOCK_KEY_ITEM" + id);
        try {
            // 1 - Tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            if (!isLock) {
                log.info("LOCK WAIT ITEM PLEASE....");
                return course;
            }
            // Get cache
            course = redisService.getObject(genCourseItemKey(id), CourseDto.class);
            // 2. YES

            if (course != null) {
                log.info("FROM CACHE EXIST {}", course);
                return course;
            }
            // 3 -> Van khong co thi truy van DB

            Course courseEntity = courseRepository.findById(id).orElse(null);
            assert courseEntity != null;
            course = CourseDto.builder()
                    .id(courseEntity.getId())
                    .name(courseEntity.getName())
                    .description(courseEntity.getDescription())
                    .startDate(courseEntity.getStartDate())
                    .state(courseEntity.getState())
                    .thumbnail(courseEntity.getThumbnail())
                    .categoryId(courseEntity.getCategory().getId())
                    .build();
            log.info("FROM DBS ->>>> {}", course);
            if (course == null) { // Neu trong dbs van khong co thi return ve not exists;
                log.info("COURSE NOT EXITS....");
                // set
                redisService.setObject(genCourseItemKey(id), course);
                return course;
            }

            // neu co thi set redis
            redisService.setObject(genCourseItemKey(id), course); // TTL
            return course;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            locker.unlock();
        }
    }



    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
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

    public Page<Course> getCourseByTeacherId(Long teacherId, Pageable pageable) {
        if (AuthService.getCurrentUser().getRole() != Role.TEACHER && AuthService.getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        log.info("Pageable: " + pageable);
        return courseRepository.findByTeacherId(teacherId, pageable);
    }

    private String genCourseItemKey(Long itemId) {
        return "COURSE:ITEM:" + itemId;
    }
}
