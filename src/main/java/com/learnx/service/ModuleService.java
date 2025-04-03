package com.learnx.service;

import com.learnx.entity.Module;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final ResourcesRepository resourcesRepository;
    private final LectureRepository lectureRepository;
    private final QuizRepository quizRepository;

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public Optional<Module> getModuleById(Long id) {
        Optional<Module> module = moduleRepository.findById(id);
        if (module.isEmpty()) {
            throw new ResourceNotFoundException("Module with id " + id + " not found!");
        }
        return module;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Module saveModule(Module module) {
        return moduleRepository.save(module);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Module deleteModule(Long id) {
        Optional<Module> module = moduleRepository.findById(id);
        module.ifPresent(moduleRepository::delete);
        return module.orElse(null);
    }

    public List<Module> findAllByCourseId(Long courseId) {
        return moduleRepository.findAllByCourseId(courseId);
    }

}
