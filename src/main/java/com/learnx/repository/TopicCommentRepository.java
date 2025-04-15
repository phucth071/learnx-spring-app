package com.learnx.repository;

import com.learnx.entity.TopicComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicCommentRepository extends JpaRepository<TopicComment, Long> {

    Optional<TopicComment> findById(Long id);

    List<TopicComment> findAllByTopicId(Long topicId, Sort sort);

    void deleteAllByTopicId(Long topicId);

}
