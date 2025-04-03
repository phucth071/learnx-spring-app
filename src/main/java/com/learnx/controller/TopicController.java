package com.learnx.controller;

import com.learnx.dto.TopicDto;
import com.learnx.entity.Topic;
import com.learnx.response.Response;
import com.learnx.service.ForumService;
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

    private final ForumService forumService;

    private final UserService userService;

    @GetMapping("")
    public Response getAllTopic(){
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all topic successfully!").data(topicService.getAllTopics()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{topicId}")
    public Response getTopicById(@PathVariable Long topicId){
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get topic by id " + topicId + " successfully!").data(topicService.getTopicById(topicId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createTopic(@RequestBody TopicDto topicDto){
        try {
            Topic topic = Topic.builder()
                    .forum(forumService.getForumById(topicDto.getForumId()).get())
                    .account(userService.getUserById(topicDto.getAccountId()))
                    .content(topicDto.getContent())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create topic successfully!").data(topicService.saveTopic(topic)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{topicId}")
    public Response editTopic(@PathVariable Long topicId, @RequestBody TopicDto topicDto){
        try {
            Optional<Topic> optionalTopic = topicService.getTopicById(topicId);
            Topic topic = convertTopicDTO(topicDto, optionalTopic);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit topic with id " + topicId + " successfully!").data(topicService.saveTopic(topic)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{topicId}")
    public Response deleteTopic(@PathVariable("topicId") Long topicId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete topic with id " + topicId + " successfully!").data(topicService.deleteTopic(topicId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private Topic convertTopicDTO(TopicDto topicDto, Optional<Topic> optionalTopic) {
        Topic topic = optionalTopic.get();
        if (topicDto.getContent() != null) topic.setContent(topicDto.getContent());
        if(topicDto.getForumId() != null) topic.setForum(forumService.getForumById(topicDto.getForumId()).get());
        if(topicDto.getAccountId() != null) topic.setAccount(userService.getUserById(topicDto.getAccountId()));
        return topic;
    }

}
