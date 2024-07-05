package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.RefreshToken;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.repository.RefreshTokenRepository;
import com.hcmute.utezbe.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        Optional<User> optUser = userRepository.findByEmailIgnoreCase(email);
        if(optUser.isPresent()) {
            User user = optUser.get();
            Optional<RefreshToken> optRefreshToken = refreshTokenRepository.findByUser(user);
            optRefreshToken.ifPresent(token -> refreshTokenRepository.delete(token));
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(userRepository.findByEmailIgnoreCase(email).get())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(600000))
                    .build();
            return refreshTokenRepository.save(refreshToken);
        }
        return null;
    }

    public Optional<RefreshToken> findByToken( String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + "Refresh Token was expired. Please make a new signin request!");
        }
        return token;
    }
}