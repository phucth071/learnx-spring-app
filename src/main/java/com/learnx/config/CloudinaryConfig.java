package com.learnx.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary("cloudinary://826394252787179:Kma9awobrG49oG85PRYjzj3x3HE@dnarlcqth");
    }
}
