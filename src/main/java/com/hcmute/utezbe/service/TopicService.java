package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.Topic;
import com.hcmute.utezbe.repository.TopicCommentRepository;
import com.hcmute.utezbe.repository.TopicRepository;
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
        return topicRepository.findById(id);
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
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
