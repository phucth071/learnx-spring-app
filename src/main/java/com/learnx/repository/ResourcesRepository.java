package com.learnx.repository;

import com.learnx.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    Optional<Resources> findById(Long id);

    List<Resources> findAllByModuleId(Long moduleId);

}
