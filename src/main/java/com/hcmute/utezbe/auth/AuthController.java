package com.hcmute.utezbe.auth;

import com.hcmute.utezbe.auth.request.*;
import com.hcmute.utezbe.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<?> oauthAuthenticate(@RequestBody IdTokenRequest IdTokenRequest, HttpServletResponse response) throws IOException {
        System.out.println("TOKEN REQUEST:::" + IdTokenRequest.getIdToken());
        return ResponseEntity.ok(service.loginWithGoogle(IdTokenRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(service.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(service.confirmOTP(token));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestParam String email) {
        return ResponseEntity.ok(service.resendOTP(email));
    }


    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return ResponseEntity.ok(principal);
    }

}
