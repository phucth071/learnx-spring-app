package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.ConfirmToken;
import com.hcmute.utezbe.repository.ConfirmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmTokenService {
    private final ConfirmTokenRepository repository;

    public void saveConfirmationToken(ConfirmToken token) {
        repository.save(token);
    }

    public Optional<ConfirmToken> getToken(String token) {
        return repository.findByToken(token);
    }

    public Optional<ConfirmToken> getTokenByUser(Long id) {
        return repository.findByUserId(id);
    }

    public int setConfirmedAt(String token) {
        return repository.updateConfirmedAt(token, java.time.LocalDateTime.now());
    }

    public void delete(ConfirmToken token) {
        repository.delete(token);
    }
}
