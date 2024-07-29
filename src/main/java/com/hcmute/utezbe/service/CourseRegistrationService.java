package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.CourseRegistration;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.entity.enumClass.State;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.repository.CourseRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseRegistrationService {
    private final CourseRegistrationRepository repository;

    public Optional<CourseRegistration> getCourseRegistration(Long studentId, Long courseId) {
        return repository.findByStudentIdAndCourseId(studentId, courseId);
    }

    public CourseRegistration save(CourseRegistration courseRegistration) {
        if (courseRegistration.getState() == null) {
            courseRegistration.setState(State.PENDING);
        }
        return repository.save(courseRegistration);
    }

    public void delete(CourseRegistration courseRegistration) {
        repository.delete(courseRegistration);
    }

    public Page<CourseRegistration> getCourseRegistrationsByStudentId(Long studentId, Pageable pageable) {
        return repository.findByStudentId(studentId, pageable);
    }

    public Page<CourseRegistration> getCourseRegistrationsByCourseId(Long courseId, Pageable pageable) {
        return repository.findByCourseId(courseId, pageable);
    }



    public List<CourseRegistration> getAllCourseRegistrations() {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return repository.findAll();
    }
    public Page<CourseRegistration> getAllCourseRegistrations(Pageable pageable) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return repository.findAll(pageable);
    }

    public CourseRegistration toggleCourseRegistration(Long studentId, Long courseId) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<CourseRegistration> otp = getCourseRegistration(studentId, courseId);
        if (otp.isPresent()) {
            CourseRegistration courseRegistration = otp.get();
            if (courseRegistration.getState().equals("PENDING")) {
                courseRegistration.setState(State.ACCEPTED);
            } else {
                courseRegistration.setState(State.PENDING);
            }
            return save(courseRegistration);
        }
        return null;
    }
}
