package com.learnx.service;

import com.learnx.entity.Forum;
import com.learnx.entity.Topic;
import com.learnx.entity.TopicComment;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.ForumRepository;
import com.learnx.repository.TopicCommentRepository;
import com.learnx.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
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

    public Optional<Forum> getForumById(Long id) {
        Optional<Forum> forum = forumRepository.findById(id);
        if (forum.isEmpty()) {
            throw new ResourceNotFoundException("Forum with id " + id + " not found!");
        }
        return forum;
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
            List<Topic> topics = topicRepository.findAllByForumId(f.getId());
            for (Topic topic : topics) {
                List<TopicComment> topicComments = topicCommentRepository.findAllByTopicId(topic.getId());
                topicCommentRepository.deleteAll(topicComments);
                topicRepository.delete(topic);
            }
            forumRepository.delete(f);
        });
        return forum.orElse(null);
    }

}
