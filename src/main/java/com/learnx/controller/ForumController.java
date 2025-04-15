package com.learnx.controller;

import com.learnx.dto.ForumDto;
import com.learnx.entity.Forum;
import com.learnx.response.Response;
import com.learnx.service.CourseService;
import com.learnx.service.ForumService;
import com.learnx.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
public class ForumController {

    private final CourseService courseService;
    private final TopicService topicService;
    private final ForumService forumService;

    @GetMapping("")
    public Response<?> getAllForum() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all forum successfully!").data(forumService.getAllForums()).build();
    }

    @GetMapping("/{forumId}")
    public Response<?> getForumById(@PathVariable("forumId") Long forumId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get forum with id " + forumId + " successfully!").data(forumService.getForumByCourseId(forumId)).build();
    }

    @GetMapping("/course/{courseId}")
    public Response<?> getForumByCourseId(@PathVariable("courseId") Long courseId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get forum with course id " + courseId + " successfully!").data(forumService.getForumByCourseId(courseId)).build();
    }

    @PostMapping("")
    public Response<?> createForum(@RequestBody ForumDto forumDto) {
        Forum forum = Forum.builder()
                .course(courseService.getCourseById(forumDto.getCourseId()).orElseThrow())
                .description(forumDto.getDescription())
                .title(forumDto.getTitle())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create forum successfully!").data(forumService.saveForum(forum)).build();
    }

    @GetMapping("/forum/{forumId}/topics")
    public Response<?> getTopicsByForumId(@PathVariable("forumId") Long forumId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get topics by forum id " + forumId + " successfully!").data(topicService.getTopicsByForumId(forumId)).build();
    }

    @PatchMapping("/{forumId}")
    public Response<?> editForum(@PathVariable("forumId") Long forumId, @RequestBody ForumDto forumDto) {
        Optional<Forum> optionalForum = forumService.getForumById(forumId);
        Forum forum = optionalForum.orElseThrow();
        forum = convertForumDTO(forumDto, optionalForum);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit forum with id " + forumId + " successfully!").data(forumService.saveForum(forum)).build();
    }

    @DeleteMapping("/{forumId}")
    public Response<?> deleteForum(@PathVariable("forumId") Long forumId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete forum with id " + forumId + " successfully!").data(forumService.deleteForum(forumId)).build();
    }

    private Forum convertForumDTO(ForumDto forumDto, Optional<Forum> optionalForum) {
        Forum forum = optionalForum.orElse(null);
        if (forum == null) {
            return null;
        }
        if(forumDto.getTitle() != null) forum.setTitle(forumDto.getTitle());
        if(forumDto.getCourseId() != null) forum.setCourse(courseService.getCourseById(forumDto.getCourseId()).orElseThrow());
        return forum;
    }

}
