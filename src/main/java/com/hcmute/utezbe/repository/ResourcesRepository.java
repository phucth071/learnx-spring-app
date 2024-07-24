package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long> {
    Optional<Resources> findById(Long id);
}
