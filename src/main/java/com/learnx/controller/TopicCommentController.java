package com.learnx.controller;

import com.learnx.dto.TopicCommentDto;
import com.learnx.entity.TopicComment;
import com.learnx.response.Response;
import com.learnx.service.TopicCommentService;
import com.learnx.service.TopicService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/topiccomments")
@RequiredArgsConstructor
public class TopicCommentController {

    private final TopicCommentService topicCommentService;
    private final TopicService topicService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{topicCommentId}")
    public Response<?> getTopicCommentById(@PathVariable Long topicCommentId){
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get toppic comment by id " + topicCommentId + " successfully!").data(topicCommentService.getTopicCommentById(topicCommentId)).build();
    }

    @PostMapping("")
    public Response<?> createTopicComment(@RequestBody TopicCommentDto topicCommentDto){
        TopicComment topicComment = TopicComment.builder()
                .topic(topicService.getTopicById(topicCommentDto.getTopicId()).orElseThrow(() -> new RuntimeException("Topic not found!")))
                .account(userService.getUserById(topicCommentDto.getAccountId()))
                .content(topicCommentDto.getContent())
                .build();
        TopicComment savedTopicComment = topicCommentService.saveTopicComment(topicComment);

        messagingTemplate.convertAndSend("/topic/comments", new HashMap<String, Object>() {{
            put("topicId", savedTopicComment.getTopic().getId());
            put("comment", savedTopicComment);
        }});

        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create topic comment successfully!").data(savedTopicComment).build();
    }

    @PatchMapping("/{topicCommentId}")
    public Response<?> editTopicComment(@PathVariable Long topicCommentId, @RequestBody TopicCommentDto topicCommentDto){
        Optional<TopicComment> optionalTopicComment = topicCommentService.getTopicCommentById(topicCommentId);
        TopicComment topicComment = optionalTopicComment.orElseThrow(() -> new RuntimeException("Topic comment not found!"));
        topicComment = convertTopicCommentDTO(topicCommentDto, optionalTopicComment);

        TopicComment savedTopicComment = topicCommentService.saveTopicComment(topicComment);

        messagingTemplate.convertAndSend("/topic/comments", new HashMap<String, Object>() {{
            put("topicId", savedTopicComment.getTopic().getId());
            put("comment", savedTopicComment);
        }});

        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit topic comment with id " + topicCommentId + " successfully!").data(savedTopicComment).build();
    }

    @DeleteMapping("/{topicCommentId}")
    public Response<?> deleteTopicComment(@PathVariable("topicCommentId") Long topicCommentId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete topic comment with id " + topicCommentId + " successfully!").data(topicCommentService.deleteTopicComment(topicCommentId)).build();
    }

    private TopicComment convertTopicCommentDTO(TopicCommentDto topicCommentDto, Optional<TopicComment> optionalTopicComment) {
        TopicComment topicComment = optionalTopicComment.get();
        if (topicCommentDto.getContent() != null) topicComment.setContent(topicCommentDto.getContent());
        if(topicCommentDto.getTopicId() != null) topicComment.setTopic(topicService.getTopicById(topicCommentDto.getTopicId()).get());
        if(topicCommentDto.getAccountId() != null) topicComment.setAccount(userService.getUserById(topicCommentDto.getAccountId()));
        return topicComment;
    }

}
