package com.hcmute.utezbe.repository;

import com.hcmute.utezbe.entity.ConfirmToken;
import com.hcmute.utezbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmTokenRepository extends JpaRepository<ConfirmToken, Long> {
    Optional<ConfirmToken> findByUser(User user);
    Optional<ConfirmToken> findByUserId(Long id);
    Optional<ConfirmToken> findByToken(String token);

    @Transactional@Modifying
    @Query("UPDATE ConfirmToken c set c.confirmedAt = ?2 where c.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
