package com.learnx.repository;

import com.learnx.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findById(Long id);

    List<Lecture> findAllByModuleId(Long moduleId);

}
