package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.ForgotPasswordToken;
import com.hcmute.utezbe.repository.ForgotPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final ForgotPasswordRepository repository;

    public ForgotPasswordToken findByTokenAndUserId(String token, Long userId) {
        return repository.findByTokenAndUserId(token, userId).orElse(null);
    }

    public ForgotPasswordToken findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    public void saveForgotPasswordToken(ForgotPasswordToken token) {
        repository.save(token);
    }

    public void delete(ForgotPasswordToken token) {
        repository.delete(token);
    }
}
