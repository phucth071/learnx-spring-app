package com.learnx.service;

import com.learnx.entity.*;
import com.learnx.entity.Module;
import com.learnx.exception.ResourceNotFoundException;

import com.learnx.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Category deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        category.ifPresent(cate -> {
            List<Course> courses = courseRepository.findByCategoryId(cate.getId());
            for (Course course : courses) {
                Forum forum = forumRepository.findByCourseId(course.getId()).orElse(null);
                if (forum != null) {
                    List<Topic> topics = topicRepository.findAllByForumId(forum.getId());
                    for (Topic topic : topics) {
                        List<TopicComment> topicComments = topicCommentRepository.findAllByTopicId(topic.getId(), Sort.by(Sort.Direction.DESC, "createdAt"));
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
