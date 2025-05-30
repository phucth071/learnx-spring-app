package com.learnx.runner;

import com.learnx.entity.User;
import com.learnx.entity.enumClass.Role;
import com.learnx.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
public class InitUserAdmin implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitUserAdmin.class);
    private static final String ROOT_ADMIN_USERNAME = "Admin";
    private static final String ROOT_ADMIN_EMAIL = "phucth0710+admin@gmail.com";
    private static final String DEFAULT_PASSWORD = "@rootadmin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitUserAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            createRootAdminIfNotExists();
        } catch (Exception e) {
            logger.error("Error during root admin initialization", e);
        }
    }

    private void createRootAdminIfNotExists() {
        Optional<User> existingAdmin = userRepository.findByFullName(ROOT_ADMIN_USERNAME);

        if (existingAdmin.isEmpty()) {
            logger.info("Root admin user not found. Creating new root admin user.");

            User rootAdmin = new User();
            rootAdmin.setFullName(ROOT_ADMIN_USERNAME);
            rootAdmin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            rootAdmin.setEmail(ROOT_ADMIN_EMAIL);
            rootAdmin.setEnabled(true);
            rootAdmin.setRole(Role.ADMIN);
            rootAdmin.setAvatarUrl("https://res.cloudinary.com/dnarlcqth/image/upload/v1735279491/clx8ebkbsfeqeqrt1wvf.png");

            userRepository.save(rootAdmin);
            logger.info("Root admin user created successfully.");
        } else {
            logger.info("Root admin user already exists. Skipping creation.");
        }
    }
}
