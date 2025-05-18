package com.learnx.controller;

import com.learnx.entity.QuestionOption;
import com.learnx.request.*;
import com.learnx.response.Response;
import com.learnx.service.QuestionOptionService;
import com.learnx.service.QuestionService;
import com.learnx.service.QuizQuestionService;
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
    private final QuizQuestionService quizQuestionService;

    @GetMapping("")
    public Response<?> getQuestionsByType(@RequestParam("type") String type) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get questions by type successfully!").data(questionService.getQuestionsByType(type)).build();
    }

    @GetMapping("/{questionId}")
    public Response<?> getQuestionById(@PathVariable("questionId") Long questionId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get question with id " + questionId + " successfully!").data(questionService.findById(questionId)).build();
    }

    @PostMapping("")
    public Response<?> createQuizQuestion(@RequestBody CreateQuestionQuizRequest request) {
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create question quiz successfully!").data(quizQuestionService.addExistedQuestionToQuiz(request.getQuizId(), request.getQuestionId())).build();
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

    @PostMapping("/tfq")
    public Response<?> createTFQ(@RequestBody CreateSCQRequest request) {
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create question successfully!").data(questionService.saveTFQ(request)).build();
    }

    @PatchMapping("/tfq/{questionId}")
    public Response<?> editTFQ(@PathVariable("questionId") Long questionId, @RequestBody CreateSCQRequest request) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question successfully!").data(questionService.updateTFQ(questionId, request)).build();
    }

    @PostMapping("/fitb")
    public Response<?> createFITB(@RequestBody CreateFITBRequest request) {
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create question successfully!").data(questionService.saveFITB(request)).build();
    }

    @PatchMapping("/fitb/{questionId}")
    public Response<?> editFITB(@PathVariable("questionId") Long questionId, @RequestBody CreateFITBRequest request) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit question successfully!").data(questionService.updateFITB(questionId, request)).build();
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
