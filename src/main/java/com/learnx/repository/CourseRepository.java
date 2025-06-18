package com.learnx.repository;

import com.learnx.entity.Course;
import com.learnx.entity.CourseRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndDeletedFalse(Long id);

    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    Page<Course> findAllPageable(Pageable pageable);

    List<Course> findByCategoryIdAndDeletedFalse(Long categoryId);

    Page<Course> findByTeacherIdAndDeletedFalse(Long teacherId, Pageable pageable);

    Page<Course> findByCourseRegistrationsInAndDeletedFalse(List<CourseRegistration> courseRegistrations, Pageable pageable);

    Page<Course> findByIdInAndDeletedFalse(List<Long> ids, Pageable pageable);
    List<Course> findByIdInAndDeletedFalse(List<Long> ids);
}
