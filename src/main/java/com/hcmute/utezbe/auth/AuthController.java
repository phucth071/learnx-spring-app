package com.hcmute.utezbe.auth;

import com.hcmute.utezbe.auth.request.AuthenticationRequest;
import com.hcmute.utezbe.auth.request.IdTokenRequest;
import com.hcmute.utezbe.auth.request.RefreshTokenRequest;
import com.hcmute.utezbe.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity<?> oauthAuthenticate(@RequestBody IdTokenRequest idTokenRequest, HttpServletResponse response) {
        System.out.println("TOKEN REQUEST:::" + idTokenRequest.getIdToken());
        return ResponseEntity.ok(service.loginWithGoogle(idTokenRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(service.refreshToken(refreshTokenRequest));
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return ResponseEntity.ok(principal);
    }

}
