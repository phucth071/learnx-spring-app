package com.hcmute.utezbe;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
public class UtezBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtezBeApplication.class, args);

	}
}
