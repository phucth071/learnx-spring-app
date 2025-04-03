package com.learnx.repository;

import com.learnx.entity.AssignmentSubmission;
import com.learnx.entity.embeddedId.AssignmentSubmissionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    void deleteById(AssignmentSubmissionId id);

    List<AssignmentSubmission> findAllByAssignmentId(Long assignmentId);

    @Query("SELECT a FROM Course a WHERE a.state = 'OPEN'")
    Page<AssignmentSubmission> findAllPageable(Pageable pageable);

    @Query("SELECT asub FROM AssignmentSubmission asub JOIN asub.assignment a JOIN a.module m JOIN m.course c WHERE c.id = :courseId ORDER BY COALESCE(asub.updatedAt, asub.createdAt) DESC")
    List<AssignmentSubmission> findAllByCourseId(Long courseId);
}
