package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.UserDto;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public Response getUserInfo(Principal principal) {
        if(principal == null) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("User not found!").build();
        }
        var currentUser = userService.findByEmailIgnoreCase(principal.getName());
        System.out.println("Principal: " + principal.getName());
        UserDto userDto = UserDto.builder()
                .fullName(currentUser.get().getFullName())
                .email(currentUser.get().getEmail())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get user info successfully!").data(userDto).build();
    }
}
