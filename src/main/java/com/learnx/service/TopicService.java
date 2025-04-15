package com.learnx.service;

import com.learnx.entity.Topic;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.TopicCommentRepository;
import com.learnx.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicCommentRepository topicCommentRepository;

    public Optional<Topic> getTopicById(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        if (topic.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return topic;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public List<Topic> getTopicsByForumId(Long forumId) {
        return topicRepository.findAllByForumId(forumId);
    }

    public Topic saveTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic deleteTopic(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        topic.ifPresent(t -> {
            topicCommentRepository.deleteAllByTopicId(t.getId());
            topicRepository.delete(t);
        });
        return topic.orElse(null);
    }

}
