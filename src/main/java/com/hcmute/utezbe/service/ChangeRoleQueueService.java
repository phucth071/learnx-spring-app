package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.ChangeRoleQueue;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.repository.ChangeRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChangeRoleQueueService {
    private final ChangeRoleRepository repository;

    public ChangeRoleQueue save(ChangeRoleQueue changeRoleQueue) {
        return repository.save(changeRoleQueue);
    }

    public ChangeRoleQueue findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public ChangeRoleQueue createChangeRoleQueue(ChangeRoleQueue changeRoleQueue) {
        if (repository.findByUserEmail(changeRoleQueue.getUser().getEmail()).isPresent()) {
            throw new RuntimeException("You have already sent a request to change role!");
        }
        return repository.save(changeRoleQueue);
    }

    public ChangeRoleQueue findByUserEmail(String email) {
        return repository.findByUserEmail(email).orElse(null);
    }

    public void deleteByUserEmail(String email) {
        repository.deleteByUserEmail(email);
    }
}
