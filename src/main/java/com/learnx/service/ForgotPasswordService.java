package com.learnx.service;

import com.learnx.entity.ForgotPasswordToken;
import com.learnx.repository.ForgotPasswordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final ForgotPasswordRepository repository;

    public ForgotPasswordToken findByTokenAndUserId(String token, Long userId) {
        return repository.findByTokenAndUserId(token, userId).orElse(null);
    }

    public ForgotPasswordToken findByUserId(Long userId) {
        return repository.findByUserId(userId).orElse(null);
    }

    public ForgotPasswordToken findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    public void saveForgotPasswordToken(ForgotPasswordToken token) {
        repository.save(token);
    }

    @Transactional
    public void delete(ForgotPasswordToken token) {
        repository.delete(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }
}
