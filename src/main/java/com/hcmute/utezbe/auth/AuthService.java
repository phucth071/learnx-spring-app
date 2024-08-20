package com.hcmute.utezbe.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.hcmute.utezbe.auth.request.AuthenticationRequest;
import com.hcmute.utezbe.auth.request.RefreshTokenRequest;
import com.hcmute.utezbe.auth.request.RegisterRequest;
import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.auth.request.IdTokenRequest;
import com.hcmute.utezbe.entity.ConfirmToken;
import com.hcmute.utezbe.entity.enumClass.Provider;
import com.hcmute.utezbe.entity.RefreshToken;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.exception.AuthenticationException;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.security.jwt.JWTService;
import com.hcmute.utezbe.service.ConfirmTokenService;
import com.hcmute.utezbe.service.JavaMailService;
import com.hcmute.utezbe.service.RefreshTokenService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response register(RegisterRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Email is not valid!").success(false).build();
        }
        Optional<User> opt = userService.findByEmailIgnoreCase(request.getEmail());
        if (opt.isPresent()) {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Email already exists!").success(false).build();
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
        return Response.builder().code(HttpStatus.OK.value()).message("Register Successfully! Please check your email").success(true).build();
    }

    public Response confirmOTP(String token) {
        Optional<ConfirmToken> otpConfirmToken = confirmTokenService.getToken(token);
        if (otpConfirmToken.isEmpty()) {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Invalid OTP!").success(false).build();
        }
        ConfirmToken confirmToken = otpConfirmToken.get();
        if (confirmToken.getConfirmedAt() != null) {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Account already confirmed!").success(false).build();
        }
        if (confirmToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("OTP is expired! Please request another OTP").success(false).build();
        }
        confirmTokenService.setConfirmedAt(token);
        userService.enableUser(confirmToken.getUser().getEmail());
        return Response.builder().code(HttpStatus.OK.value()).message("Account confirmed successfully!").success(true).build();
    }

    public Response resendOTP(String email) {
        User user = userService.findByEmailIgnoreCase(email).orElseThrow(() -> new AuthenticationException("User not found"));
        if (user.isEnabled()) {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Account already confirmed!").success(false).build();
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
                        .put("fullName", user.getFullName())
                        .put("email", user.getEmail())
                        .put("avatar", user.getAvatarUrl())
                        .put("role", user.getRole().name())
                )
                .message("Login Successfully!")
                .build();
    }


    @Transactional
    public Response loginWithGoogle(IdTokenRequest idTokenRequest) {
        String googleClientId = "660554863773-c5na5d9dvnogok0i23ekgnpr15to3rvn.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singleton(googleClientId))
                .build();
        GoogleIdToken token;
        try {
            token = verifier.verify(idTokenRequest.getIdToken());
            if (Objects.isNull(token)) {
                throw new AuthenticationException("Invalid id token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        .put("email", user.getEmail())
                        .put("full_name", user.getFullName())
                        .put("avatar", user.getAvatarUrl())
                        .put("role", user.getRole().name())
                )
                .build();
    }

    private User verifyGoogleIdToken(String idToken) {
        String googleClientId = "660554863773-c5na5d9dvnogok0i23ekgnpr15to3rvn.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singleton(googleClientId))
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
                            .message("Refresh Token Successfully!")
                            .build();
                })
                .orElseThrow(() -> new RuntimeException(
                        "Refresh Token not in database!"));
    }

    public static boolean isUserHaveRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role.name())));
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
