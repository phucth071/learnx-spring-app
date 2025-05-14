package com.learnx.controller;

import com.learnx.dto.QuestionDto;
import com.learnx.entity.QuestionOption;
import com.learnx.request.CreateMCQRequest;
import com.learnx.request.CreateSCQRequest;
import com.learnx.request.SwapQuestionOptionRequest;
import com.learnx.response.Response;
import com.learnx.service.QuestionOptionService;
import com.learnx.service.QuestionService;
import com.learnx.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/question-quizzes")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;
    private final QuestionOptionService questionOptionService;

    @GetMapping("")
    public Response<?> getAllQuestion() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all quiz question successfully!").data(questionService.getAllQuestions()).build();
    }

    @GetMapping("/{questionId}")
    public Response<?> getQuestionById(@PathVariable("questionId") Long questionId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get question with id " + questionId + " successfully!").data(questionService.getQuestionById(questionId)).build();
    }

    @PostMapping("/mcq")
    public Response<?> createMCQ(@RequestBody CreateMCQRequest request) {
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create question successfully!").data(questionService.saveMCQ(request)).build();
    }

    @PatchMapping("/mcq/{questionId}")
    public Response<?> editMCQ(@PathVariable("questionId") Long questionId, @RequestBody CreateMCQRequest request) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question successfully!").data(questionService.updateMCQ(questionId, request)).build();
    }

    @PostMapping("/scq")
    public Response<?> createSCQ(@RequestBody CreateSCQRequest request) {
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create question successfully!").data(questionService.saveSCQ(request)).build();
    }

    @PatchMapping("/scq/{questionId}")
    public Response<?> editSCQ(@PathVariable("questionId") Long questionId, @RequestBody CreateSCQRequest request) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question successfully!").data(questionService.updateSCQ(questionId, request)).build();
    }

    @PatchMapping("/options/{questionId}/{optionId}")
    public Response<?> editOption(
            @PathVariable("questionId") Long questionId,
            @PathVariable("optionId") Long optionId,
            @RequestBody QuestionOption questionOption) {
        QuestionOption existingOption = questionOptionService.findByQuestionId(questionId, optionId);
        if (existingOption == null) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Question option with id " + optionId + " not found!").build();
        }
        existingOption.setContent(questionOption.getContent());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question option with id " + optionId + " successfully!").data(questionOptionService.save(existingOption)).build();
    }

    @PatchMapping("/options/swap")
    public Response<?> swapOptions(@RequestBody SwapQuestionOptionRequest req) {
        questionOptionService.swapOptions(req.getQuestionId(), req.getOptionIdSrc(), req.getOptionIdDest());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Swap question options successfully!").build();
    }

    @DeleteMapping("/{questionId}")
    public Response<?> deleteQuestion(@PathVariable("questionId") Long questionId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete question with id " + questionId + " successfully!").data(questionService.deleteQuestion(questionId)).build();
    }
}
