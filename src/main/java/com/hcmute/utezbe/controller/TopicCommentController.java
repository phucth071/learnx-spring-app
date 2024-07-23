package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.TopicCommentDto;
import com.hcmute.utezbe.entity.TopicComment;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.TopicCommentService;
import com.hcmute.utezbe.service.TopicService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/topiccomments")
@RequiredArgsConstructor
public class TopicCommentController {

    private final TopicCommentService topicCommentService;

    private final TopicService topicService;

    private final UserService userService;

    @GetMapping("")
    public Response getAllTopicComment(){
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all topic comment successfully!").data(topicCommentService.getAllTopicComments()).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all topic comment failed!").data(null).build();
        }
    }

    @GetMapping("/{topicCommentId}")
    public Response getTopicCommentById(@PathVariable Long topicCommentId){
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get toppic comment by id " + topicCommentId + " successfully!").data(topicCommentService.getTopicCommentById(topicCommentId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get toppic comment by " + topicCommentId + " failed!").data(null).build();
        }
    }

    @PostMapping("")
    public Response createTopicComment(@RequestBody TopicCommentDto topicCommentDto){
        try {
            TopicComment topicComment = TopicComment.builder()
                    .topic(topicService.getTopicById(topicCommentDto.getTopicId()).get())
                    .account(userService.getUserById(topicCommentDto.getAccountId()))
                    .content(topicCommentDto.getContent())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create topic comment successfully!").data(topicCommentService.saveTopicComment(topicComment)).build();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create topic comment failed!").data(null).build();
        }
    }

    @PatchMapping("/{topicCommentId}")
    public Response editTopicComment(@PathVariable Long topicCommentId, @RequestBody TopicCommentDto topicCommentDto){
        try {
            Optional<TopicComment> optionalTopicComment = topicCommentService.getTopicCommentById(topicCommentId);
            if (!optionalTopicComment.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Topic comment with id " + topicCommentId + " not found!").data(null).build();
            }
            TopicComment topicComment = optionalTopicComment.get();
            if(topicComment != null) {
                topicComment = convertTopicCommentDTO(topicCommentDto, optionalTopicComment);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit topic comment with id " + topicCommentId + " successfully!").data(topicCommentService.saveTopicComment(topicComment)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Topic comment with id " + topicCommentId + " not found!").data(null).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit topic comment with id " + topicCommentId + " failed!").data(null).build();
        }
    }

    @DeleteMapping("/{topicCommentId}")
    public Response deleteTopicComment(@PathVariable("topicCommentId") Long topicCommentId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete topic comment with id " + topicCommentId + " successfully!").data(topicCommentService.deleteTopicComment(topicCommentId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete topic comment with id " + topicCommentId + " failed!").data(null).build();
        }
    }

    private TopicComment convertTopicCommentDTO(TopicCommentDto topicCommentDto, Optional<TopicComment> optionalTopicComment) {
        TopicComment topicComment = optionalTopicComment.get();
        if (topicCommentDto.getContent() != null) topicComment.setContent(topicCommentDto.getContent());
        if(topicCommentDto.getTopicId() != null) topicComment.setTopic(topicService.getTopicById(topicCommentDto.getTopicId()).get());
        if(topicCommentDto.getAccountId() != null) topicComment.setAccount(userService.getUserById(topicCommentDto.getAccountId()));
        return topicComment;
    }

}
