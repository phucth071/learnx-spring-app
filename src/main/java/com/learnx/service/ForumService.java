package com.learnx.service;

import com.learnx.entity.Forum;
import com.learnx.entity.Topic;
import com.learnx.entity.TopicComment;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.ForumRepository;
import com.learnx.repository.TopicCommentRepository;
import com.learnx.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumRepository forumRepository;
    private final TopicRepository topicRepository;
    private final TopicCommentRepository topicCommentRepository;
    private final CourseService courseService;

    public Optional<Forum> getForumById(Long id) {
        Optional<Forum> forum = forumRepository.findById(id);
        if (forum.isEmpty()) {
            throw new ResourceNotFoundException("Forum with id " + id + " not found!");
        }
        return forum;
    }


    public Forum getForumByCourseId(Long courseId) {
        Forum forum = forumRepository.findByCourseId(courseId).orElse(null);
        if (forum == null) {
            forum = createForumWithCourseId(courseId, "Default Forum", "Default Description");
        }
        return forum;
    }

    public Forum createForumWithCourseId(Long courseId, String title, String description) {
        Forum forum = Forum.builder()
                .course(courseService.getCourseById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course with id " + courseId + " not found!")))
                .title(title)
                .description(description)
                .build();
        return forumRepository.save(forum);
    }

    public List<Forum> getAllForums() {
        return forumRepository.findAll();
    }

    public Forum saveForum(Forum forum) {
        return forumRepository.save(forum);
    }

    @Transactional
    public Forum deleteForum(Long id) {
        Optional<Forum> forum = forumRepository.findById(id);
        forum.ifPresent(f -> {
            List<Topic> topics = topicRepository.findAllByForumId(forum.get().getId(), Sort.by(Sort.Direction.DESC, "createdAt"));
            for (Topic topic : topics) {
                List<TopicComment> topicComments = topicCommentRepository.findAllByTopicId(topic.getId(), Sort.by(Sort.Direction.DESC, "createdAt"));
                topicCommentRepository.deleteAll(topicComments);
                topicRepository.delete(topic);
            }
            forumRepository.delete(f);
        });
        return forum.orElse(null);
    }

}
