package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findById(Long id);

    List<Assignment> findAllByModuleId(Long moduleId);

    @Query("SELECT a FROM Course a WHERE a.state = 'OPEN'")
    Page<Assignment> findAllPageable(Pageable pageable);

    @Query("SELECT a FROM Assignment a JOIN a.module m JOIN m.course c JOIN c.courseRegistrations cr WHERE cr.email = ?1")
    List<Assignment> findAllByEmail(String email);
}
