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

//        // Set cookie
//        if (authResponse.isSuccess()) {
//            ObjectNode data = (ObjectNode) authResponse.getData();
//            var accessToken = data.get("accessToken").asText();
//            var refreshToken = data.get("refreshToken").asText();
//
//            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("access_token", accessToken)
//                    .httpOnly(true)
//                    .maxAge(60 * 60 * 24 * 7)
//                    .path("/")
//                    .build().toString());
//            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refresh_token", refreshToken)
//                    .httpOnly(true)
//                    .maxAge(60 * 60 * 24 * 7)
//                    .path("/")
//                    .build().toString());
//        }
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<?> oauthAuthenticate(@RequestBody IdTokenRequest IdTokenRequest, HttpServletResponse response) throws IOException {
        System.out.println("TOKEN REQUEST:::" + IdTokenRequest.getIdToken());
        var authResponse = service.loginWithGoogle(IdTokenRequest);

//        if (authResponse.isSuccess()) {
//            ObjectNode data = (ObjectNode) authResponse.getData();
//            var accessToken = data.get("accessToken").asText();
//            var refreshToken = data.get("refreshToken").asText();
//
//            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("access_token", accessToken)
//                    .httpOnly(true)
//                    .maxAge(60 * 60 * 24 * 7)
//                    .path("/")
//                    .build().toString());
//            response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refresh_token", refreshToken)
//                    .httpOnly(true)
//                    .maxAge(60 * 60 * 24 * 7)
//                    .path("/")
//                    .build().toString());
//        }
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

    @PostMapping("/handle-change-role/{id}")
    public ResponseEntity<?> handleChangeRole(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.changeRoleQueueForId(id));
    }
}
