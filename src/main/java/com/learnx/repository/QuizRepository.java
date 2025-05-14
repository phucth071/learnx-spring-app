package com.learnx.repository;

import com.learnx.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findById(Long id);

    @Query("SELECT q FROM Quiz q WHERE q.module.id = ?1")
    List<Quiz> findAllByModuleId(Long id);

}
