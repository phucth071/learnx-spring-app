package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.AssignmentSubmission;
import com.hcmute.utezbe.entity.embeddedId.AssignmentSubmissionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    Optional<AssignmentSubmission> findById(AssignmentSubmissionId id);

    void deleteById(AssignmentSubmissionId id);

    List<AssignmentSubmission> findAllByAssignmentId(Long assignmentId);

    @Query("SELECT a FROM Course a WHERE a.state = 'OPEN'")
    Page<AssignmentSubmission> findAllPageable(Pageable pageable);

}
