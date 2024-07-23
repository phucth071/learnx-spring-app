package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.TopicComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicCommentRepository extends JpaRepository<TopicComment, Long> {
    Optional<TopicComment> findById(Long id);
}
