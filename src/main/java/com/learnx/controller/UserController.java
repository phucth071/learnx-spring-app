package com.learnx.controller;

import com.learnx.auth.AuthService;
import com.learnx.auth.request.ChangePasswordRequest;
import com.learnx.dto.AuthUserDto;
import com.learnx.dto.UserDto;
import com.learnx.entity.ChangeRoleQueue;
import com.learnx.entity.User;
import com.learnx.entity.enumClass.State;
import com.learnx.request.ChangeRoleQueueRequest;
import com.learnx.request.UserPatchRequest;
import com.learnx.response.Response;
import com.learnx.service.ChangeRoleQueueService;
import com.learnx.service.CloudinaryService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuthService authService;
    private final ChangeRoleQueueService changeRoleQueueService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    public Response<?> getAllUsers() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all users successfully!").data(userService.findAll()).build();
    }


    @GetMapping("/info")
    public Response getUserInfo(Principal principal) {
        if(principal == null) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("User not found!").build();
        }
        var currentUser = userService.findByEmailIgnoreCase(principal.getName());
        AuthUserDto userDto = AuthUserDto.convertToDto(currentUser.get());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get user info successfully!").data(userDto).build();
    }

    @GetMapping("/{email}")
    public Response getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmailIgnoreCase(email);
        if (user.isEmpty()) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("User not found!").build();
        }
        UserDto userDto = UserDto.convertToDto(user.get());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get user info successfully!").data(userDto).build();
    }

    @PatchMapping(value = "", consumes = {"multipart/form-data"})
    public Response<?> patchUser(@RequestPart("user") UserPatchRequest req,
                              @RequestPart("avatar") @Nullable MultipartFile avatar) {
        User user = userService.findByEmailIgnoreCase(req.getEmail()).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }
        if (user.getId() != AuthService.getCurrentUser().getId()) {
            throw new RuntimeException("You are not allowed to do this action!");
        }
        if (req.getFullName() != null) {
            user.setFullName(req.getFullName());
        }
        if (req.getEmail() == null || !req.getEmail().equals(user.getEmail())) {
            throw new RuntimeException("Email cannot be changed!");
        }
        if (avatar != null) {
            String avatarUrl = cloudinary.upload(avatar);
            user.setAvatarUrl(avatarUrl);
        }
        UserDto userDto = UserDto.convertToDto(userService.save(user));
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Update user successfully!").data(userDto).build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            return ResponseEntity.ok(authService.changePassword(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Change password failed!").build());
    }

    @PostMapping("/change-role/request")
    public ResponseEntity<?> changeRoleRequest(@RequestBody ChangeRoleQueueRequest request) {
        ChangeRoleQueue existChangeRoleQueue = changeRoleQueueService.findByUserEmail(request.getEmail());
        if (existChangeRoleQueue != null) {
            return ResponseEntity.ok(Response.builder().code(200).success(false).message("Yêu cầu đang phê duyệt. Vui lòng chờ!").build());
        }
        ChangeRoleQueue changeRoleQueue = ChangeRoleQueue.builder()
                .newRole(request.getNewRole())
                .oldRole(request.getOldRole())
                .status(State.PENDING)
                .user(userService.findByEmailIgnoreCase(request.getEmail()).orElse(null))
                .build();
        return ResponseEntity.created(null).body(changeRoleQueueService.save(changeRoleQueue));
    }

}
