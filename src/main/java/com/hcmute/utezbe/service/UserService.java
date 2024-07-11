package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
    public User getUserByFullName(String fullName) {
        return userRepository.findByFullName(fullName).orElse(null);
    }

}
