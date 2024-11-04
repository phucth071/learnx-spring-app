package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Resources;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourcesService {

    private final ResourcesRepository resourcesRepository;

    public Optional<Resources> getResourcesById(Long id) {
        Optional<Resources> resources = resourcesRepository.findById(id);
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return resources;
    }

    public List<Resources> getAllResources() {
        return resourcesRepository.findAll();
    }

    public Resources saveResources(Resources resources) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return resourcesRepository.save(resources);
    }

    public Resources deleteResources(Long id) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Resources> resources = resourcesRepository.findById(id);
        resources.ifPresent(resourcesRepository::delete);
        return resources.orElse(null);
    }

    public List<Resources> getAllResourcesByModuleId(Long moduleId) {
        return resourcesRepository.findAllByModuleId(moduleId);
    }
}
