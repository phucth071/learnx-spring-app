package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
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

    @Query("SELECT a FROM Assignment a JOIN a.module m JOIN m.course c JOIN c.courseRegistrations cr WHERE cr.email = ?1 ORDER BY a.endDate DESC")
    List<Assignment> findTop3ByStudentIdOrderByDateTimeDesc(String email);

    @Query("SELECT a FROM Assignment a JOIN a.module m JOIN m.course c JOIN c.courseRegistrations cr WHERE cr.email = ?1 AND MONTH(a.endDate) = ?2 AND YEAR(a.endDate) = ?3")
    List<Assignment> findAllByEmailAndEndDateMonthYear(String email, int month, int year);

    @Query(value = "SELECT a.* FROM assignment a JOIN Module m ON a.module_id = m.id JOIN Course c ON m.course_id = c.id JOIN course_registration cr ON c.id = cr.course_id WHERE cr.email = ?1 AND a.end_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL ?2 DAY) AND MONTH(a.end_date) = ?3 AND YEAR(a.end_date) = ?4", nativeQuery = true)
    List<Assignment> findAssignmentByNextXDay(String email, int day, int month, int year);

    @Query("SELECT a FROM Assignment a JOIN a.module m JOIN m.course c JOIN c.courseRegistrations cr WHERE cr.email = ?1 AND a.title LIKE %?2%")
    List<Assignment> findAssignmentsByEmailAndTitleContaining(String email, String keyword);

    List<Assignment> findByEndDate(Date endDate);
    List<Assignment> findByEndDateBetween(Date startDate, Date endDate);
}