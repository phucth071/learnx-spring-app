package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.entity.Assignment;
import com.learnx.entity.AssignmentSubmission;
import com.learnx.entity.User;
import com.learnx.repository.AssignmentRepository;
import com.learnx.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public List<Assignment> getAssignmentsEndingOn(LocalDate date) {
        java.util.Date startDate = java.util.Date.from(date.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        java.util.Date endDate = java.util.Date.from(date.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
        return assignmentRepository.findByEndDateBetween(startDate, endDate);
    }

    public List<Assignment> getAllAssignmentsByModuleId(Long moduleId) {
        return assignmentRepository.findAllByModuleId(moduleId);
    }

    public List<Assignment> getTop3AssignmentsByStudentId() {
        User user = AuthService.getCurrentUser();
        return assignmentRepository.findTop3ByStudentIdOrderByDateTimeDesc(user.getEmail());
    }

    public List<Assignment> getAllAssignmentsByStudentIdAndEndDateMonthYear(int month, int year) {
        User user = AuthService.getCurrentUser();
        return assignmentRepository.findAllByEmailAndEndDateMonthYear(user.getEmail(), month, year);
    }

    public List<Assignment> getAssignmentByNextXDay(int day, int month, int year) {
        User user = AuthService.getCurrentUser();
        return assignmentRepository.findAssignmentByNextXDay(user.getEmail(), day, month, year);
    }

    public List<Assignment> getAssignmentsByEmailAndTitleContaining(String keyword) {
        User user = AuthService.getCurrentUser();
        return assignmentRepository.findAssignmentsByEmailAndTitleContaining(user.getEmail(), keyword);
    }
}
