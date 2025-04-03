package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.dto.UserDto;
import com.learnx.entity.User;
import com.learnx.repository.UserRepository;
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

    public User saveUser(User user) {
        User dbUser = userRepository.findByEmailIgnoreCase(user.getEmail()).orElse(null);
        if (dbUser != null) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists!");
        }
        return userRepository.save(user);
    }

    public User patchUser(UserDto user) {
        User dbUser = userRepository.findByEmailIgnoreCase(user.getEmail()).orElse(null);
        if (dbUser == null) {
            throw new RuntimeException("User with email " + user.getEmail() + " not found!");
        }
        if (AuthService.getCurrentUser().getId() != dbUser.getId()) {
            throw new RuntimeException("You do not have permission to do this action!");
        }
        dbUser.setFullName(user.getFullName());
        dbUser.setAvatarUrl(user.getAvatar());
        dbUser.setRole(user.getRole());
        return userRepository.save(dbUser);
    }
}
