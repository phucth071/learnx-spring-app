package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.AssignmentSubmission;
import com.hcmute.utezbe.entity.embeddedId.AssignmentSubmissionId;
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
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public Optional<AssignmentSubmission> getAssignmentSubmissionById(Long assignmentId, Long studentId) {
        AssignmentSubmissionId id = new AssignmentSubmissionId(assignmentId, studentId);
        return assignmentSubmissionRepository.findById(id);
    }

    public List<AssignmentSubmission> getAllAssignmentSubmissions() {
        return assignmentSubmissionRepository.findAll();
    }

    public Page<AssignmentSubmission> getAllAssignmentSubmissionsPageable(Pageable pageable) {
        return assignmentSubmissionRepository.findAllPageable(pageable);
    }

    public AssignmentSubmission saveAssignmentSubmission(AssignmentSubmission assignmentSubmission) {
        return assignmentSubmissionRepository.save(assignmentSubmission);
    }

    public AssignmentSubmission updateAssignmentSubmission(AssignmentSubmission existingSubmission, AssignmentSubmission updatedAssignmentSubmission) {
        existingSubmission.setScore(updatedAssignmentSubmission.getScore());
        existingSubmission.setTextSubmission(updatedAssignmentSubmission.getTextSubmission());
        existingSubmission.setFileSubmissionUrl(updatedAssignmentSubmission.getFileSubmissionUrl());
        existingSubmission.setLinkSubmission(updatedAssignmentSubmission.getLinkSubmission());
        return assignmentSubmissionRepository.save(existingSubmission);
    }

    @Transactional
    public void deleteAssignmentSubmission(Long assignmentId, Long studentId) {
        AssignmentSubmissionId id = new AssignmentSubmissionId(assignmentId, studentId);
         assignmentSubmissionRepository.deleteById(id);
    }

}
