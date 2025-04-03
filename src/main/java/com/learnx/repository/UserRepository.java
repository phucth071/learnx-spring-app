package com.learnx.repository;

import com.learnx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByFullName(String fullName);
    Optional<User> findById(long id);

    @Transactional@Modifying
    @Query("UPDATE User u set u.isEnabled = true where u.email = ?1")
    int enableUser(String email);
}
