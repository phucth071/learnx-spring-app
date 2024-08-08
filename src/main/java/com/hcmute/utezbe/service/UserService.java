package com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
    public User save(User user) {
        return userRepository.save(user);
    }
    public User getUserByFullName(String fullName) {
        return userRepository.findByFullName(fullName).orElse(null);
    }
    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}
