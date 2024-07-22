package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.Forum;
import com.hcmute.utezbe.repository.ForumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumRepository forumRepository;

    public Optional<Forum> getForumById(Long id) {
        return forumRepository.findById(id);
    }

    public List<Forum> getAllForums() {
        return forumRepository.findAll();
    }

    public Forum saveForum(Forum forum) {
        return forumRepository.save(forum);
    }

    public Forum deleteForum(Long id) {
        Optional<Forum> forum = forumRepository.findById(id);
        forum.ifPresent(forumRepository::delete);
        return forum.orElse(null);
    }

}
