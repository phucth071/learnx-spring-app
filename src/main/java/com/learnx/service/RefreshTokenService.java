package com.learnx.service;

import com.learnx.entity.RefreshToken;
import com.learnx.entity.User;
import com.learnx.exception.AuthenticationException;
import com.learnx.repository.RefreshTokenRepository;
import com.learnx.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        Optional<User> optUser = userRepository.findByEmailIgnoreCase(email);
        if(optUser.isPresent()) {
            User user = optUser.get();
            Optional<RefreshToken> optRefreshToken = refreshTokenRepository.findByUser(user);
            optRefreshToken.ifPresent(refreshTokenRepository::delete);
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(7L * 24 * 60 * 60 * 1000))
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
            throw new AuthenticationException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId).orElse(null);
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}