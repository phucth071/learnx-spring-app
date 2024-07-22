package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findById(Long id);
}
