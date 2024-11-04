package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    Optional<Module> findById(Long id);

    Page<Module> findByCourseId(Long courseId, Pageable pageable);
    List<Module> findAllByCourseId(Long courseId);

}
