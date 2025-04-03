package com.learnx.repository;

import com.learnx.entity.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, Long> {
    Optional<ForgotPasswordToken> findByTokenAndUserId(String token, Long userId);
    Optional<ForgotPasswordToken> findByUserId(Long userId);
    Optional<ForgotPasswordToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
