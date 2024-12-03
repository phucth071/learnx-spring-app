package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.ChangeRoleQueue;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.repository.ChangeRoleRepository;
import lombok.AllArgsConstructor;
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

    public ChangeRoleQueue createChangeRoleQueue(ChangeRoleQueue changeRoleQueue) {
        if (AuthService.getCurrentUser().getRole() != Role.ADMIN) {
            if (changeRoleQueue.getUser().getId() != AuthService.getCurrentUser().getId()) {
                throw new RuntimeException("You are not allowed to do this action!");
            }
        }
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
