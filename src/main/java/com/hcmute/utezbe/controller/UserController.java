package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
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
        System.out.println("Principal: " + principal.getName());
        var currentUser = userService.getUserByEmail(principal.getName());
        if(currentUser == null) {
            return Response.builder().error(true).success(false).message("User not found!").build();
        }
        return Response.builder().error(false).success(true).message("Get user info successfully!").data(principal).build();
    }
}
