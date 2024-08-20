package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.entity.AssignmentSubmission;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.repository.AssignmentRepository;
import com.hcmute.utezbe.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException();
        }
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment deleteAssignment(Long id) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException();
        }
        Optional<Assignment> assignments = assignmentRepository.findById(id);
        assignments.ifPresent(a -> {
            List<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository.findAllByAssignmentId(a.getId());
            assignmentSubmissionRepository.deleteAll(assignmentSubmissions);
            assignmentRepository.delete(a);
        });
        return assignments.orElse(null);
    }

}
