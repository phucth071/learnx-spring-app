package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.Topic;
import com.hcmute.utezbe.entity.TopicComment;
import com.hcmute.utezbe.repository.TopicCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicCommentService {

    private final TopicCommentRepository topicCommentRepository;

    public Optional<TopicComment> getTopicCommentById(Long id) {
        return topicCommentRepository.findById(id);
    }

    public List<TopicComment> getAllTopicComments() {
        return topicCommentRepository.findAll();
    }

    public TopicComment saveTopicComment(TopicComment topicComment) {
        return topicCommentRepository.save(topicComment);
    }

    public TopicComment deleteTopicComment(Long id) {
        Optional<TopicComment> topicComment = topicCommentRepository.findById(id);
        topicComment.ifPresent(topicCommentRepository::delete);
        return topicComment.orElse(null);
    }

}
