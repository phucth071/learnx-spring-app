package com.learnx.repository;

import com.learnx.entity.ChangeRoleQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChangeRoleRepository extends JpaRepository<ChangeRoleQueue, Long> {
    Optional<ChangeRoleQueue> findByUserEmail(String email);
    void deleteByUserEmail(String email);
}
