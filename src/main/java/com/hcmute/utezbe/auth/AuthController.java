package com.hcmute.utezbe.auth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hcmute.utezbe.auth.request.*;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request, HttpServletResponse response
    ) {
        var authResponse = service.authenticate(request);

        // Set cookie
        if (authResponse.isSuccess()) {
            ObjectNode data = (ObjectNode) authResponse.getData();
            var accessToken = data.get("accessToken").asText();
            var refreshToken = data.get("refreshToken").asText();

            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("access_token", accessToken)
                    .httpOnly(true)
                    .maxAge(60 * 60 * 24 * 7)
                    .path("/")
                    .build().toString());
            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .maxAge(60 * 60 * 24 * 7)
                    .path("/")
                    .build().toString());
        }
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<?> oauthAuthenticate(@RequestBody IdTokenRequest IdTokenRequest, HttpServletResponse response) throws IOException {
        System.out.println("TOKEN REQUEST:::" + IdTokenRequest.getIdToken());
        var authResponse = service.loginWithGoogle(IdTokenRequest);
        if (authResponse.isSuccess()) {
            ObjectNode data = (ObjectNode) authResponse.getData();
            var accessToken = data.get("accessToken").asText();
            var refreshToken = data.get("refreshToken").asText();

            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("access_token", accessToken)
                    .httpOnly(true)
                    .maxAge(60 * 60 * 24 * 7)
                    .path("/")
                    .build().toString());
            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .maxAge(60 * 60 * 24 * 7)
                    .path("/")
                    .build().toString());
        }
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
    public ResponseEntity<?> resendOTP(@RequestBody ResendOTPRequest request) {
        return ResponseEntity.ok(service.resendOTP(request.getEmail()));
    }

    @PostMapping("logout/{userId}")
    public Response logout(Principal principal, @PathVariable("userId") Long userId) {
        var currentUser = userService.findByEmailIgnoreCase(principal.getName());
        if(principal == null || currentUser.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        if (currentUser.get().getId() != userId) {
            throw new RuntimeException("User not found!");
        }
        service.logout(userId);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Logout successfully!").build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ResendOTPRequest req) {
        return ResponseEntity.ok(service.sendForgotPasswordToken(req.getEmail()));
    }

    @PostMapping("/forgot-password/confirm")
    public ResponseEntity<?> confirmForgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }
}
