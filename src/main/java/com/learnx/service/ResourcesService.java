package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.entity.Resources;
import com.learnx.entity.enumClass.Role;
import com.learnx.exception.AccessDeniedException;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.ResourcesRepository;
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
        if (AuthService.isUserNotHaveRole(Role.TEACHER) && AuthService.isUserNotHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return resourcesRepository.save(resources);
    }

    public Resources deleteResources(Long id) {
        if (AuthService.isUserNotHaveRole(Role.TEACHER) && AuthService.isUserNotHaveRole(Role.ADMIN)) {
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
