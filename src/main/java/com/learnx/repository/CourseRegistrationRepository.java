package com.learnx.repository;

import com.learnx.entity.CourseRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {

    Optional<CourseRegistration> findByEmailAndCourseId(String email, Long courseId);

    Page<CourseRegistration> findByCourseId(Long courseId, Pageable pageable);
    List<CourseRegistration> findByCourseId(Long courseId);

    void deleteAllByCourseId(Long courseId);

    Page<CourseRegistration> findByEmail(String email, Pageable pageable);
    List<CourseRegistration> findByEmail(String email);
}
