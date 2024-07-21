package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findById(Long id);

    @Query("SELECT c FROM Course c WHERE c.state = 'OPEN'")
    Page<Course> findAllPageable(Pageable pageable);
}
