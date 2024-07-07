package com.hcmute.utezbe.auth;

import com.hcmute.utezbe.auth.request.AuthenticationRequest;
import com.hcmute.utezbe.auth.request.IdTokenRequest;
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

    @PostMapping("/oauth2/oauth-authenticate")
    public ResponseEntity<?> oauthAuthenticate(@RequestBody IdTokenRequest idTokenRequest, HttpServletResponse response) {
        String token = service.loginWithGoogle(idTokenRequest);
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", token);
        return ResponseEntity.ok(Response.builder().error(false).success(true).message("Login successfully!").data(tokenResponse).build());
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return ResponseEntity.ok(principal);
    }

}
