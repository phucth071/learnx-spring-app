package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.AuthUserDto;
import com.hcmute.utezbe.dto.UserDto;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CloudinaryService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CloudinaryService cloudinary;
    @GetMapping("/info")
    public Response getUserInfo(Principal principal) {
        if(principal == null) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("User not found!").build();
        }
        var currentUser = userService.findByEmailIgnoreCase(principal.getName());
        System.out.println("Principal: " + principal.getName());
        AuthUserDto userDto = AuthUserDto.builder()
                .fullName(currentUser.get().getFullName())
                .email(currentUser.get().getEmail())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get user info successfully!").data(userDto).build();
    }

//    TODO: Implement the HASH REQUEST to check if the request is sent multiple times
    @PatchMapping("/{userId}")
    public Response patchUser(@PathVariable Long userId,
                              @RequestParam("fullName") String fullName,
                              @RequestParam("email") String email,
                              @RequestPart("avatar") @Nullable MultipartFile avatar) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                throw new RuntimeException("User not found!");
            }
            if (fullName != null) {
                user.setFullName(fullName);
            }
            if (email == null || !email.equals(user.getEmail())) {
                throw new RuntimeException("Email cannot be changed!");
            }
            if (avatar != null) {
                String avatarUrl = cloudinary.upload(avatar);
                user.setAvatarUrl(avatarUrl);
            }
            UserDto userDto = UserDto.convertToDto(userService.save(user));
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Update user successfully!").data(userDto).build();
        } catch (Exception e) {
            throw e;
        }
    }

//    TODO: Implement USER change password (request = ChangePasswordRequest

//    TODO: Implement USER forgot password
}
