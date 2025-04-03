package com.learnx.service;

import com.learnx.entity.AssignmentSubmission;
import com.learnx.entity.User;
import com.learnx.entity.embeddedId.AssignmentSubmissionId;
import com.learnx.repository.AssignmentSubmissionRepository;
import com.learnx.response.AssignmentSubmissionWStudentInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final NotificationService notificationService;

    public Optional<AssignmentSubmission> getAssignmentSubmissionByAssignmentIdAndStudentId(Long assignmentId, Long studentId) {
        return assignmentSubmissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);

    }

    public List<AssignmentSubmission> getAllAssignmentSubmissions() {
        return assignmentSubmissionRepository.findAll();
    }

    public List<AssignmentSubmissionWStudentInfoResponse> getAllByAssignmentId(Long assignmentId) {
        List<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository.findAllByAssignmentId(assignmentId);
        if (assignmentSubmissions != null) {
            return assignmentSubmissions.stream().map(assignmentSubmission -> {
                User student = assignmentSubmission.getStudent();
                return AssignmentSubmissionWStudentInfoResponse.builder()
                        .assignmentId(assignmentSubmission.getAssignment().getId())
                        .studentId(student.getId())
                        .studentName(student.getFullName())
                        .studentEmail(student.getEmail())
                        .score(assignmentSubmission.getScore())
                        .textSubmission(assignmentSubmission.getTextSubmission())
                        .fileSubmissionUrl(assignmentSubmission.getFileSubmissionUrl())
                        .linkSubmission(assignmentSubmission.getFileSubmissionUrl())
                        .createdAt(assignmentSubmission.getCreatedAt().toString())
                        .updatedAt(assignmentSubmission.getUpdatedAt() != null ? assignmentSubmission.getUpdatedAt().toString() : null)
                        .build();
            }).toList();
        }
        return null;
    }

    public Page<AssignmentSubmission> getAllAssignmentSubmissionsPageable(Pageable pageable) {
        return assignmentSubmissionRepository.findAllPageable(pageable);
    }

    public List<AssignmentSubmission> getAllAssignmentSubmissionsByCourseId(Long courseId) {
        return assignmentSubmissionRepository.findAllByCourseId(courseId);
    }

    @Transactional
    public AssignmentSubmission saveAssignmentSubmission(AssignmentSubmission assignmentSubmission) {
        return assignmentSubmissionRepository.save(assignmentSubmission);
    }

    public AssignmentSubmission updateAssignmentSubmission(AssignmentSubmission existingSubmission, AssignmentSubmission updatedAssignmentSubmission) {
        existingSubmission.setScore(updatedAssignmentSubmission.getScore());
        existingSubmission.setTextSubmission(updatedAssignmentSubmission.getTextSubmission());
        existingSubmission.setFileSubmissionUrl(updatedAssignmentSubmission.getFileSubmissionUrl());
//        existingSubmission.setLinkSubmission(updatedAssignmentSubmission.getLinkSubmission());
        return assignmentSubmissionRepository.save(existingSubmission);
    }

    @Transactional
    public void deleteAssignmentSubmission(Long assignmentId, Long studentId) {
        AssignmentSubmissionId id = new AssignmentSubmissionId(assignmentId, studentId);
         assignmentSubmissionRepository.deleteById(id);
    }

}
