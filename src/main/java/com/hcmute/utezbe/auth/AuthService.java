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
import com.hcmute.utezbe.dto.UserDto;
import com.hcmute.utezbe.entity.*;
import com.hcmute.utezbe.entity.enumClass.Provider;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.entity.enumClass.State;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.exception.AuthenticationException;
import com.hcmute.utezbe.request.ChangeRoleQueueRequest;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.security.jwt.JWTService;
import com.hcmute.utezbe.service.*;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailValidator emailValidator;
    private final ConfirmTokenService confirmTokenService;
    private final JavaMailService emailService;
    private final ForgotPasswordService forgotPasswordService;
    private final ChangeRoleQueueService changeRoleQueueService;

    private static final String CLIENT_ID = "660554863773-c5na5d9dvnogok0i23ekgnpr15to3rvn.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:3000";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserDto register(RegisterRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new RuntimeException("Email không hợp lệ!");
        }
        Optional<User> opt = userService.findByEmailIgnoreCase(request.getEmail());
        if (opt.isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.STUDENT)
                .provider(Provider.DATABASE)
                .isEnabled(false)
                .avatarUrl("https://res.cloudinary.com/dnarlcqth/image/upload/v1735279491/clx8ebkbsfeqeqrt1wvf.png")
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
        String message = "Mã OTP để xác nhận tài khoản của bạn là: <span>" + token + "</span>"
                + "<br>OTP sẽ hết hạn sau 15 phút!"
                + "<br>Hoặc ấn vào liên kết sau để xác nhận: <a href='http://localhost:3000/register/verify?otp=" + token + "&email=" + user.getEmail() + "'>Xác nhận</a>";
        emailService.send(request.getEmail(), buildEmailOTP(request.getFullName(), message));
        return UserDto.convertToDto(user);
    }

    public Response<?> confirmOTP(String token, String email) {
        Optional<ConfirmToken> otpConfirmToken = confirmTokenService.getToken(token);
        if (otpConfirmToken.isEmpty()) {
            throw new RuntimeException("Mã xác nhận không chính xác!");
        }
        ConfirmToken confirmToken = otpConfirmToken.get();
        if (confirmToken.getConfirmedAt() != null) {
            throw new RuntimeException("Tài khoản đã được xác nhận!");
        }
        if (confirmToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác nhận đã hết hạn!");
        }
        if (!confirmToken.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Mã xác nhận không chính xác!");
        }
        confirmTokenService.setConfirmedAt(token);
        userService.enableUser(confirmToken.getUser().getEmail());
        return Response.builder().code(HttpStatus.OK.value()).message("Xác nhận thành công!").success(true).build();
    }


    public Response<?> resendOTP(String email) {
        System.out.println("EMAIL:" + email);
        User user = userService.findByEmailIgnoreCase(email).orElseThrow(() -> new AuthenticationException("Không tìm thấy tài khoản!"));
        if (user.getProvider() == Provider.GOOGLE) {
            throw new AuthenticationException("Tài khoản đã đăng ký bằng Google! Vui lòng đăng nhập bằng Google!");
        }
        if (user.isEnabled()) {
            throw new RuntimeException("Tài khoản đã được xác nhận!");
        }
        ConfirmToken oldToken = confirmTokenService.getTokenByUser(user.getId()).get();
        confirmTokenService.delete(oldToken);
        String newOtp = generateOTP();
        ConfirmToken confirmToken = ConfirmToken.builder()
                .token(newOtp)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
        confirmTokenService.saveConfirmationToken(confirmToken);
        String message = "Mã OTP để xác nhận tài khoản của bạn là: <span>" + newOtp + "</span>";
        emailService.send(email, buildEmailOTP(user.getFullName(), message));
        return Response.builder().code(HttpStatus.OK.value()).message("Gửi mã xác nhận thành công! Vui lòng kiểm tra hòm thư của bạn").success(true).build();
    }


    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    private String buildEmailOTP(String name, String message) {
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
                "      <h2> " + message + "</h2>\n" +
                "      <h3>Trân trọng!</h3>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";
    }

    @Transactional
    public Response<?> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Sai tên đăng nhập hoặc mật khẩu!");
        }

        Optional<User> opt = userService.findByEmailIgnoreCase(request.getEmail());
        if(opt.isEmpty()) {
            throw new AuthenticationException("Người dùng không tồn tại!");
        }
        User user = opt.get();
        if(user.getProvider() == Provider.GOOGLE) {
            throw new AuthenticationException("Tài khoản đã đăng ký bằng Google! Vui lòng đăng nhập bằng Google!");
        }
        if(!user.isEnabled()) {
            throw new AuthenticationException("Tài khoản chưa xác nhận bằng OTP!");
        }
//        if(user.getRole() != request.getRole()) {
//            return Response.builder().error(true).success(false).message("You Do Not Have Authorize").build();
//        };
        var jwtToken = jwtService.generateAccessToken(user);
        RefreshToken oldRefreshToken = refreshTokenService.findByUserId(user.getId());
        if (oldRefreshToken != null) {
            refreshTokenService.deleteByToken(oldRefreshToken.getToken());
        }
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .data(objectMapper.createObjectNode()
                        .putObject("user")
                        .put("accessToken", jwtToken)
                        .put("refreshToken", refreshToken.getToken())
                        .put("fullName", user.getFullName())
                        .put("email", user.getEmail())
                        .put("avatar", user.getAvatarUrl())
                        .put("role", user.getRole().name())
                )
                .message("Login Successfully!")
                .build();
    }

    @Transactional
    public Response<?> loginWithGoogle(IdTokenRequest idTokenRequest) throws IOException, GeneralSecurityException {
//        GoogleTokenResponse tokenResponse = exchangeCode(authCode.getAuthCode());
        User user = verifyGoogleIdToken(idTokenRequest.getIdToken());
        if (user == null) {
            throw new AuthenticationException("Có lỗi xảy ra khi xác thực tài khoản Google!");
        }
        user = createOrUpdateUser(user);
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken oldRefreshToken = refreshTokenService.findByUserId(user.getId());
        if (oldRefreshToken != null) {
            refreshTokenService.deleteByToken(oldRefreshToken.getToken());
        }
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Login successfully!")
                .data(objectMapper.createObjectNode()
                        .put("accessToken", accessToken)
                        .put("refreshToken", refreshToken.getToken())
                        .put("email", user.getEmail())
                        .put("fullName", user.getFullName())
                        .put("avatar", user.getAvatarUrl())
                        .put("role", user.getRole().name())
                )
                .build();
    }

    public Response<?> logout(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User not found!");
        }
        if (userId != AuthService.getCurrentUser().getId()) {
            throw new RuntimeException("You are not allowed to do this action!");
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

    public Response<?> changePassword(ChangePasswordRequest request) {
        User user = userService.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
        if (!Objects.equals(user.getId(), AuthService.getCurrentUser().getId())) {
            throw new AccessDeniedException("You do not have permission to change password!");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Đổi mật khẩu thành công!")
                .build();
    }

    public Response<?> sendForgotPasswordToken(String email) {
        User user = userService.findByEmailIgnoreCase(email).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
        if (user.getProvider() != Provider.DATABASE) {
            throw new RuntimeException("Tài khoản đã đăng ký bằng Google! Vui lòng đăng nhập bằng Google!");
        }
        String token = UUID.randomUUID().toString();
        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .token(token)
                .user(user)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
        forgotPasswordService.saveForgotPasswordToken(forgotPasswordToken);
        String message = "Ấn vào liên kết sau để reset mật khẩu của bạn: <a href='http://localhost:3000/reset-password?token=" + token + "'>Reset Password</a>";
        emailService.send(email, buildEmailOTP(user.getFullName(), message));
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Gửi mã xác nhận thành công! Vui lòng kiểm tra hòm thư của bạn")
                .build();
    }

    @Transactional
    public Response<?> resetPassword(ForgotPasswordRequest request) {
        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findByToken(request.getToken());
        if (forgotPasswordToken == null) {
            throw new RuntimeException("Mã xác nhận không chính xác!");
        }
        if (forgotPasswordToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác nhận đã hết hạn!");
        }
        User user = forgotPasswordToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.save(user);

        forgotPasswordService.delete(forgotPasswordToken);

        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Đổi mật khẩu thành công!")
                .build();
    }
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



    private User verifyGoogleIdToken(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        var token = verifier.verify(idToken);
        if (Objects.isNull(token)) {
            throw new AuthenticationException("Xác thực với Google thất bại!");
        }
        var payload = token.getPayload();
        String email = payload.getEmail();
        String fullName = payload.get("name").toString();
        String avatarUrl = (String) payload.get("picture");
        Role role = Role.STUDENT;

        return User.builder()
                .email(email)
                .fullName(fullName)
                .avatarUrl(avatarUrl)
                .provider(Provider.GOOGLE)
                .role(role)
                .build();
    }

    @Transactional
    public User createOrUpdateUser(User user) {
        User existedUser = userService.findByEmailIgnoreCase(user.getEmail()).orElse(null);
        if (existedUser == null) {
            userService.save(user);
            return user;
        }
        if (existedUser.getProvider() != Provider.GOOGLE) {
            throw new AuthenticationException("Email đã đăng ký với mật khẩu! Vui lòng đăng nhập với mật khẩu");
        }
//        existedUser.setFullName(user.getFullName());
//        existedUser.setAvatarUrl(user.getAvatarUrl());
//        existedUser.setProvider(user.getProvider());
//        existedUser.setEnabled(true);
        existedUser = userService.save(existedUser);
        return existedUser == null ? user : existedUser;
    }

    @Transactional 
    public Object refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateAccessToken(user);
                    String oldToken = refreshTokenRequest.getToken();
                    refreshTokenService.deleteByToken(oldToken);
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
                    return Response.builder()
                            .data(objectMapper.createObjectNode()
                                    .put("accessToken", accessToken)
                                    .put("refreshToken", refreshToken.getToken())
                                    .put("email", user.getEmail())
                                    .put("fullName", user.getFullName())
                                    .put("avatar", user.getAvatarUrl())
                                    .put("role", user.getRole().name())
                            )
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

    public User getGoogleUserDetails(String email) {
        // Implement the logic to fetch user details from Google using the email
        // This might involve calling the Google People API or similar
        // For example:
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(email);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String fullName = (String) payload.get("name");
                String avatarUrl = (String) payload.get("picture");
                return User.builder()
                        .email(email)
                        .fullName(fullName)
                        .avatarUrl(avatarUrl)
                        .provider(Provider.GOOGLE)
                        .role(Role.STUDENT) // or determine role based on your logic
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
