package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Lecture;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    public Optional<Lecture> getLectureById(Long id) {
        Optional<Lecture> lecture = lectureRepository.findById(id);
        if (lecture.isEmpty()) {
            throw new ResourceNotFoundException("Lecture with id " + id + " not found!");
        }
        return lecture;
    }

    public List<Lecture> getAllLectures() {
        return lectureRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Lecture saveLecture(Lecture lecture) {
        return lectureRepository.save(lecture);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Lecture deleteLecture(Long id) {
        Optional<Lecture> lecture = lectureRepository.findById(id);
        lecture.ifPresent(lectureRepository::delete);
        return lecture.orElse(null);
    }

    public List<Lecture> getAllLecturesByModuleId(Long moduleId) {
        return lectureRepository.findAllByModuleId(moduleId);
    }

}
