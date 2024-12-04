package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.entity.AssignmentSubmission;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.repository.AssignmentRepository;
import com.hcmute.utezbe.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Page<Assignment> getAllAssignmentsPageable(Pageable pageable) {
        return assignmentRepository.findAllPageable(pageable);
    }

    @Transactional
    public Assignment saveAssignment(Assignment assignment) {

        return assignmentRepository.save(assignment);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Assignment deleteAssignment(Long id) {
        Optional<Assignment> assignments = assignmentRepository.findById(id);
        assignments.ifPresent(a -> {
            List<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository.findAllByAssignmentId(a.getId());
            assignmentSubmissionRepository.deleteAll(assignmentSubmissions);
            assignmentRepository.delete(a);
        });
        return assignments.orElse(null);
    }

    public List<Assignment> getAllAssignmentsLoggedInUser() {
        User user = AuthService.getCurrentUser();
        return assignmentRepository.findAllByEmail(user.getEmail());
    }

    public List<Assignment> getAllAssignmentsByModuleId(Long moduleId) {
        return assignmentRepository.findAllByModuleId(moduleId);
    }
}
