package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.CourseRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {

    Optional<CourseRegistration> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Page<CourseRegistration> findByStudentId(Long studentId, Pageable pageable);

    Page<CourseRegistration> findByCourseId(Long courseId, Pageable pageable);

    void deleteAllByCourseId(Long courseId);

    Page<CourseRegistration> findAllByStudentEmail(String email, Pageable pageable);

}
