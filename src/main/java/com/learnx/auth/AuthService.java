package com.learnx.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.learnx.auth.request.*;
import com.learnx.dto.UserDto;
import com.learnx.entity.ConfirmToken;
import com.learnx.entity.ForgotPasswordToken;
import com.learnx.entity.RefreshToken;
import com.learnx.entity.User;
import com.learnx.entity.enumClass.Role;
import com.learnx.exception.AccessDeniedException;
import com.learnx.exception.AuthenticationException;
import com.learnx.response.Response;
import com.learnx.security.jwt.JWTService;
import com.learnx.service.*;
import com.learnx.utils.RandomPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    // TODO: BUG - On google login, isEnabled should be true
    // TODO: BUG - Email not send when login first time with google

    @Value("${app.client.frontend")
    private String CLIENT_FRONTEND;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;
    private static final String REDIRECT_URI = "http://localhost:3001";
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
                + "<br>Hoặc ấn vào liên kết sau để xác nhận: <a href='http://localhost:3001/register/verify?otp=" + token + "&email=" + user.getEmail() + "'>Xác nhận</a>";
        emailService.send(request.getEmail(), buildEmailBody(request.getFullName(), message));
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
        emailService.send(email, buildEmailBody(user.getFullName(), message));
        return Response.builder().code(HttpStatus.OK.value()).message("Gửi mã xác nhận thành công! Vui lòng kiểm tra hòm thư của bạn").success(true).build();
    }


    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    private String buildEmailBody(String name, String message) {
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
    public Response<?> authenticateWithGoogle(IdTokenRequest idTokenRequest) throws IOException, GeneralSecurityException {
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
        if (!userId.equals(AuthService.getCurrentUser().getId())) {
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

        String token = UUID.randomUUID().toString();
        ForgotPasswordToken forgotPasswordToken = ForgotPasswordToken.builder()
                .token(token)
                .user(user)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
        forgotPasswordService.saveForgotPasswordToken(forgotPasswordToken);
        String message = "Ấn vào liên kết sau để reset mật khẩu của bạn: <a href='http://localhost:3001/reset-password?token=" + token + "'>Reset Password</a>";
        emailService.send(email, buildEmailBody(user.getFullName(), message));
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

        return User.builder()
                .email(email)
                .fullName(fullName)
                .avatarUrl(avatarUrl)
                .build();
    }

    @Transactional
    public User createOrUpdateUser(User user) {
        User existedUser = userService.findByEmailIgnoreCase(user.getEmail()).orElse(null);

        if (existedUser == null) {
            String password = RandomPasswordGenerator.generateCommonLangPassword();
            user.setPassword(passwordEncoder.encode(password));
            String message = "Mật khẩu tự động khởi tạo của bạn là: <span>" + password + "</span>"
                    + "<br>Hãy thay đổi sau khi đăng nhập lần đầu!";
            user.setRole(Role.STUDENT);
            emailService.send(user.getEmail(), buildEmailBody(user.getFullName(), message));
            userService.save(user);
            return user;
        }
        if (!existedUser.getAvatarUrl().equals(user.getAvatarUrl())) {
            existedUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (!existedUser.getFullName().equals(user.getFullName())) {
            existedUser.setFullName(user.getFullName());
        }

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

    public static boolean isUserNotHaveRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(role.name())));
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
