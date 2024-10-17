package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.*;
import com.hcmute.utezbe.entity.Module;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final ForumRepository forumRepository;
    private final TopicCommentRepository topicCommentRepository;
    private final TopicRepository topicRepository;
    private final ModuleRepository moduleRepository;
    private final LectureRepository lectureRepository;
    private final ResourcesRepository resourcesRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;

    public Optional<Category> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new ResourceNotFoundException("Category with id " + id + " not found!");
        }
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    public Category deleteCategory(Long id) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Category> category = categoryRepository.findById(id);
        category.ifPresent(cate -> {
            List<Course> courses = courseRepository.findByCategoryId(cate.getId());
            for (Course course : courses) {
                Forum forum = forumRepository.findByCourseId(course.getId());
                if (forum != null) {
                    List<Topic> topics = topicRepository.findAllByForumId(forum.getId());
                    for (Topic topic : topics) {
                        List<TopicComment> topicComments = topicCommentRepository.findAllByTopicId(topic.getId());
                        topicCommentRepository.deleteAll(topicComments);
                        topicRepository.delete(topic);
                    }
                    forumRepository.delete(forum);
                }
                List<Module> modules = moduleRepository.findAllByCourseId(course.getId());
                for (Module module : modules) {
                    List<Lecture> lectures = lectureRepository.findAllByModuleId(module.getId());
                    lectureRepository.deleteAll(lectures);

                    List<Resources> resources = resourcesRepository.findAllByModuleId(module.getId());
                    resourcesRepository.deleteAll(resources);
                    moduleRepository.delete(module);
                }
                courseRegistrationRepository.deleteAllByCourseId(course.getId());
                courseRepository.delete(course);
            }
            categoryRepository.delete(cate);
        });
        return category.orElse(null);
    }

}
