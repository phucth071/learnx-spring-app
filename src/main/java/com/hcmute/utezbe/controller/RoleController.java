package com.hcmute.utezbe.controller;


import com.hcmute.utezbe.entity.ChangeRoleQueue;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.ChangeRoleQueueService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final ChangeRoleQueueService changeRoleQueueService;
    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/request/pageable")
    public Response<?> getAllChangeRoleQueuePageable(@Nullable Pageable pageable) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all change role queue successfully!").data(changeRoleQueueService.findAllPageable(pageable)).build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/request")
    public Response<?> getAllChangeRoleQueue() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all change role queue successfully!").data(changeRoleQueueService.findAll()).build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/handle-change-role/{id}")
    public ResponseEntity<?> handleChangeRole(@PathVariable("id") Long id) {
        return ResponseEntity.ok(changeRoleQueueService.changeRoleQueueForId(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/handle-reject/{id}")
    public ResponseEntity<?> handleRejectRole(@PathVariable("id") Long id) {
        return ResponseEntity.ok(changeRoleQueueService.rejectRequest(id));
    }

}
