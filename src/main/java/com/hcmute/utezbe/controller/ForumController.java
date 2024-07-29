package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.ForumDto;
import com.hcmute.utezbe.entity.Forum;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CourseService;
import com.hcmute.utezbe.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
public class ForumController {

    private final CourseService courseService;

    private final ForumService forumService;

    @GetMapping("")
    public Response getAllForum() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all forum successfully!").data(forumService.getAllForums()).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all forum failed!").data(null).build();
        }
    }

    @GetMapping("/{forumId}")
    public Response getForumById(@PathVariable("forumId") Long forumId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get forum with id " + forumId + " successfully!").data(forumService.getForumById(forumId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get forum with id " + forumId + " failed!").data(null).build();
        }
    }

    @PostMapping("")
    public Response createForum(@RequestBody ForumDto forumDto) {
        try{
            Forum forum = Forum.builder()
                    .course(courseService.getCourseById(forumDto.getCourseId()).get())
                    .description(forumDto.getDescription())
                    .title(forumDto.getTitle())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create forum successfully!").data(forumService.saveForum(forum)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create forum failed!").data(null).build();
        }
    }

    @PatchMapping("/{forumId}")
    public Response editForum(@PathVariable("forumId") Long forumId, @RequestBody ForumDto forumDto) {
        try{
            Optional<Forum> optionalForum = forumService.getForumById(forumId);
            if (!optionalForum.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Forum with id " + forumId + " not found!").data(null).build();
            }
            Forum forum = optionalForum.get();
            if(forum != null) {
                forum = convertForumDTO(forumDto, optionalForum);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit forum with id " + forumId + " successfully!").data(forumService.saveForum(forum)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Forum with id " + forumId + " not found!").data(null).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit forum with id " + forumId + " failed!").data(null).build();
        }
    }

    @DeleteMapping("/{forumId}")
    public Response deleteForum(@PathVariable("forumId") Long forumId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete forum with id " + forumId + " successfully!").data(forumService.deleteForum(forumId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete forum with id " + forumId + " failed!").data(null).build();
        }
    }

    private Forum convertForumDTO(ForumDto forumDto, Optional<Forum> optionalForum) {
        Forum forum = optionalForum.get();
        if (forumDto.getDescription() != null) forum.setDescription(forumDto.getDescription());
        if(forumDto.getTitle() != null) forum.setTitle(forumDto.getTitle());
        if(forumDto.getCourseId() != null) forum.setCourse(courseService.getCourseById(forumDto.getCourseId()).get());
        return forum;
    }

}
