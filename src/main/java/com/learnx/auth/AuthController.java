package com.learnx.auth;

import com.learnx.auth.request.*;
import com.learnx.service.ChangeRoleQueueService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final UserService userService;
    private final ChangeRoleQueueService changeRoleQueueService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request, HttpServletResponse response
    ) {
        var authResponse = service.authenticate(request);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<?> oauthAuthenticate(@RequestBody IdTokenRequest IdTokenRequest, HttpServletResponse response) throws IOException, GeneralSecurityException {
        var authResponse = service.authenticateWithGoogle(IdTokenRequest);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(service.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmRequest confirmRequest) {
        System.out.println("OTP: " + confirmRequest.getOtp() + " Email: " + confirmRequest.getEmail());
        return ResponseEntity.ok(service.confirmOTP(confirmRequest.getOtp(), confirmRequest.getEmail()));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody EmailRequest request) {
        return ResponseEntity.ok(service.resendOTP(request.getEmail()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest req) {
        return ResponseEntity.ok(service.sendForgotPasswordToken(req.getEmail()));
    }

    @PostMapping("/forgot-password/confirm")
    public ResponseEntity<?> confirmForgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }
}
