package com.learnx.controller;

import com.learnx.dto.TopicDto;
import com.learnx.entity.Topic;
import com.learnx.response.Response;
import com.learnx.service.ForumService;
import com.learnx.service.TopicCommentService;
import com.learnx.service.TopicService;
import com.learnx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final TopicCommentService topicCommentService;
    private final ForumService forumService;
    private final UserService userService;

    @GetMapping("")
    public Response<?> getAllTopic(){
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all topic successfully!").data(topicService.getAllTopics()).build();
    }

    @GetMapping("/{topicId}")
    public Response<?> getTopicById(@PathVariable Long topicId){
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get topic by id " + topicId + " successfully!").data(topicService.getTopicById(topicId)).build();
    }

    @GetMapping("/comments/{topicId}")
    public Response<?> getTopicCommentsByTopicId(@PathVariable Long topicId){
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get topic comments by topic id " + topicId + " successfully!").data(topicCommentService.getTopicCommentsByTopicId(topicId)).build();
    }

    @PostMapping("")
    public Response<?> createTopic(@RequestBody TopicDto topicDto) {
        Topic topic = Topic.builder()
                .forum(forumService.getForumById(topicDto.getForumId()).orElseThrow(() -> new RuntimeException("Forum not found!")))
                .account(userService.getUserById(topicDto.getAccountId()))
                .content(topicDto.getContent())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create topic successfully!").data(topicService.saveTopic(topic)).build();
    }

    @PatchMapping("/{topicId}")
    public Response<?> editTopic(@PathVariable Long topicId, @RequestBody TopicDto topicDto){
        Optional<Topic> optionalTopic = topicService.getTopicById(topicId);
        Topic topic = convertTopicDTO(topicDto, optionalTopic);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit topic with id " + topicId + " successfully!").data(topicService.saveTopic(topic)).build();
    }

    @DeleteMapping("/{topicId}")
    public Response<?> deleteTopic(@PathVariable("topicId") Long topicId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete topic with id " + topicId + " successfully!").data(topicService.deleteTopic(topicId)).build();
    }

    private Topic convertTopicDTO(TopicDto topicDto, Optional<Topic> optionalTopic) {
        Topic topic = optionalTopic.get();
        if (topicDto.getContent() != null) topic.setContent(topicDto.getContent());
        if (topicDto.getForumId() != null) topic.setForum(forumService.getForumById(topicDto.getForumId()).get());
        if (topicDto.getAccountId() != null) topic.setAccount(userService.getUserById(topicDto.getAccountId()));
        return topic;
    }

}
