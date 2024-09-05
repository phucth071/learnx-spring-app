package com.hcmute.utezbe.auth;

import com.cloudinary.api.exceptions.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.hcmute.utezbe.auth.request.*;
import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.dto.UserDto;
import com.hcmute.utezbe.entity.ConfirmToken;
import com.hcmute.utezbe.entity.enumClass.Provider;
import com.hcmute.utezbe.entity.RefreshToken;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.AuthenticationException;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.security.jwt.JWTService;
import com.hcmute.utezbe.service.ConfirmTokenService;
import com.hcmute.utezbe.service.JavaMailService;
import com.hcmute.utezbe.service.RefreshTokenService;
import com.hcmute.utezbe.service.UserService;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailValidator emailValidator;
    private final ConfirmTokenService confirmTokenService;
    private final JavaMailService emailService;

    private static final String CLIENT_ID = "660554863773-c5na5d9dvnogok0i23ekgnpr15to3rvn.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:3000";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserDto register(RegisterRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new RuntimeException("Email is not valid!");
        }
        Optional<User> opt = userService.findByEmailIgnoreCase(request.getEmail());
        if (opt.isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.STUDENT)
                .provider(Provider.DATABASE)
                .isEnabled(false)
                .avatarUrl("https://res.cloudinary.com/dnarlcqth/image/upload/v1719906429/samples/landscapes/architecture-signs.jpg")
                .build();

        userService.save(user);

        String token = generateOTP();

        ConfirmToken confirmToken = ConfirmToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();

        confirmTokenService.saveConfirmationToken(confirmToken);
        emailService.send(request.getEmail(), buildEmailOTP(request.getFullName(), token));
        return UserDto.convertToDto(user);
    }

    public Response confirmOTP(String token) {
        Optional<ConfirmToken> otpConfirmToken = confirmTokenService.getToken(token);
        if (otpConfirmToken.isEmpty()) {
            throw new RuntimeException("Invalid OTP!");
        }
        ConfirmToken confirmToken = otpConfirmToken.get();
        if (confirmToken.getConfirmedAt() != null) {
            throw new RuntimeException("OTP already confirmed!");
        }
        if (confirmToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired!");
        }
        confirmTokenService.setConfirmedAt(token);
        userService.enableUser(confirmToken.getUser().getEmail());
        return Response.builder().code(HttpStatus.OK.value()).message("Account confirmed successfully!").success(true).build();
    }

    public Response resendOTP(String email) {
        User user = userService.findByEmailIgnoreCase(email).orElseThrow(() -> new AuthenticationException("User not found"));
        if (user == null) {
            throw new AuthenticationException("User not found!");
        }
        if (user.getProvider() == Provider.GOOGLE) {
            throw new AuthenticationException("Email already registered by another method!");
        }
        if (user.isEnabled()) {
            throw new RuntimeException("Account already confirmed!");
        }
        String newOtp = generateOTP();
        ConfirmToken confirmToken = ConfirmToken.builder()
                .token(newOtp)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
        confirmTokenService.saveConfirmationToken(confirmToken);
        emailService.send(email, buildEmailOTP(user.getFullName(), newOtp));
        return Response.builder().code(HttpStatus.OK.value()).message("OTP sent successfully! Please check your email").success(true).build();
    }


    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    private String buildEmailOTP(String name, String otp) {
        return "<html>\n" +
                "  <head>\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: Arial, sans-serif;\n" +
                "        background-color: #f4f4f4;\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "      .container {\n" +
                "        max-width: 600px;\n" +
                "        margin: 50px auto;\n" +
                "        padding: 20px;\n" +
                "        background-color: #fff;\n" +
                "        border-radius: 10px;\n" +
                "        box-shadow: 0 0 10px rgba(0,0,0,0.1);\n" +
                "      }\n" +
                "      h1 {\n" +
                "        color: #333;\n" +
                "      }\n" +
                "      h2 {\n" +
                "        color: #555;\n" +
                "      }\n" +
                "      span {\n" +
                "        color: #ff0000;\n" +
                "        font-weight: bold;\n" +
                "        letter-spacing: 2px;\n" +
                "        font-size: 24px;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div class=\"container\">\n" +
                "      <h1>Xin Chào, " + name + "!</h1>\n" +
                "      <h2>Mã OTP để xác nhận tài khoản của bạn là: <span>" + otp + "</span></h2>\n" +
                "      <h3>Trân trọng!</h3>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";
    }

    public Response authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Optional<User> opt = userService.findByEmailIgnoreCase(request.getEmail());
        if(opt.isEmpty()) {
            throw new AuthenticationException("User not found!");
        }
        User user = opt.get();
        if(user.getProvider() == Provider.GOOGLE) {
            throw new AuthenticationException("Email already registered by another method!");
        }
        if(!user.isEnabled()) {
            throw new AuthenticationException("Account is not confirmed yet!");
        }
//        if(user.getRole() != request.getRole()) {
//            return Response.builder().error(true).success(false).message("You Do Not Have Authorize").build();
//        };
        RequestContext.setUserId(user.getId());
        var jwtToken = jwtService.generateAccessToken(user);
        var jwtRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .data(objectMapper.createObjectNode()
                        .putObject("user")
                        .put("accessToken", jwtToken)
                        .put("refreshToken", jwtRefreshToken.getToken())
                        .put("fullName", user.getFullName())
                        .put("email", user.getEmail())
                        .put("avatar", user.getAvatarUrl())
                        .put("role", user.getRole().name())
                )
                .message("Login Successfully!")
                .build();
    }

    public Response logout(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User not found!");
        }
        RefreshToken refreshToken = refreshTokenService.findByUserId(userId);
        if (refreshToken == null) {
            throw new RuntimeException("User not found!");
        }
        refreshTokenService.deleteByToken(refreshToken.getToken());
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Logout Successfully!")
                .build();
    }

    public Response changePassword(ChangePasswordRequest request) {
        User user = userService.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getEmail() != AuthService.getCurrentUser().getEmail()) {
            throw new AccessDeniedException("You do not have permission to change password!");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Change password successfully!")
                .build();
    }

//   TODO: reset USER password using link (token = uuid)

    public GoogleTokenResponse exchangeCode(String authCode) throws IOException {
        return new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                "https://oauth2.googleapis.com/token",
                CLIENT_ID,
                CLIENT_SECRET,
                authCode,
                REDIRECT_URI)
                .execute();
    }

    @Transactional
    public Response loginWithGoogle(IdTokenRequest idTokenRequest) throws IOException {
//        GoogleTokenResponse tokenResponse = exchangeCode(authCode.getAuthCode());
        User user = verifyGoogleIdToken(idTokenRequest.getIdToken());
        if (user == null) {
            throw new AuthenticationException("Invalid id token");
        }
        user = createOrUpdateUser(user);
        RequestContext.setUserId(user.getId());
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Login successfully!")
                .data(objectMapper.createObjectNode()
                        .put("access_token", accessToken)
                        .put("refreshToken", refreshToken.getToken())
                        .put("email", user.getEmail())
                        .put("full_name", user.getFullName())
                        .put("avatar", user.getAvatarUrl())
                        .put("role", user.getRole().name())
                )
                .build();
    }

    private User verifyGoogleIdToken(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        try {
            var token = verifier.verify(idToken);
            if (Objects.isNull(token)) {
                throw new AuthenticationException("Token verification failed!");
            }
            var payload = token.getPayload();
            String email = payload.getEmail();
            String fullName = payload.get("name").toString();
            String avatarUrl = (String) payload.get("picture");
            Role role = (payload.get("hd") == null) ? Role.STUDENT :  (payload.get("hd").equals("hcmute.edu.vn") ? Role.TEACHER : Role.STUDENT);

            return User.builder()
                    .email(email)
                    .fullName(fullName)
                    .avatarUrl(avatarUrl)
                    .provider(Provider.GOOGLE)
                    .role(role)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public User createOrUpdateUser(User user) {
        User existedUser = userService.findByEmailIgnoreCase(user.getEmail()).orElse(null);
        if (existedUser == null) {
            System.out.println("Create new user");
            userService.save(user);
            return user;
        }
        if (existedUser.getProvider() != Provider.GOOGLE) {
            throw new AuthenticationException("Email already registered by another method!");
        }
        System.out.println("Update existed user");
        existedUser.setFullName(user.getFullName());
        existedUser.setAvatarUrl(user.getAvatarUrl());
        existedUser.setProvider(user.getProvider());
        existedUser.setEnabled(true);
        existedUser = userService.save(existedUser);
        return existedUser == null ? user : existedUser;
    }

    public Object refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    RequestContext.setUserId(user.getId());
                    String accessToken = jwtService.generateAccessToken(user);
                    return Response.builder()
                            .data(objectMapper.createObjectNode()
                                    .put("accessToken", accessToken)
                                    .put("refreshToken", refreshTokenRequest.getToken()))
                            .code(HttpStatus.OK.value())
                            .success(true)
                            .message("Get new token Successfully!")
                            .build();
                })
                .orElseThrow(() -> new RuntimeException(
                        "Invalid refresh token"));
    }

    public static boolean isUserHaveRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role.name())));
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public static boolean checkTeacherRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.TEACHER.name())));
    }

    public static boolean checkAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ADMIN.name())));
    }
}
