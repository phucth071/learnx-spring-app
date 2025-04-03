package com.learnx.service;

import com.learnx.entity.TopicComment;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.TopicCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicCommentService {

    private final TopicCommentRepository topicCommentRepository;

    public Optional<TopicComment> getTopicCommentById(Long id) {
        Optional<TopicComment> topicComment = topicCommentRepository.findById(id);
        if (topicComment.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return topicComment;
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
