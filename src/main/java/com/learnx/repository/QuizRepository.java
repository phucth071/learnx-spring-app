package com.learnx.repository;

import com.learnx.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q FROM Quiz q WHERE q.id.id = :id AND q.id.module_id = :moduleId")
    Optional<Quiz> findByIdAndModuleId(Long id, Long moduleId);

    List<Quiz> findAllByModuleId(Long moduleId);

    @Query("SELECT MAX(q.id.id) FROM Quiz q WHERE q.id.module_id = :moduleId")
    Long findMaxIdByModuleId(Long moduleId);
}
