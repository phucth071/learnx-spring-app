package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.RefreshToken;
import com.hcmute.utezbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Ref;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByToken(String token);
}