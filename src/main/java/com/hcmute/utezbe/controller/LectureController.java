package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.LectureDto;
import com.hcmute.utezbe.entity.Lecture;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.LectureService;
import com.hcmute.utezbe.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final ModuleService moduleService;

    @GetMapping("")
    public Response<?> getAllLecture() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all lecture successfully!").data(lectureService.getAllLectures()).build();
    }

    @GetMapping("/{lectureId}")
    public Response<?> getLectureById(@PathVariable("lectureId") Long lectureId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get lecture with id " + lectureId + " successfully!").data(lectureService.getLectureById(lectureId)).build();
    }

    @PostMapping("")
    public Response<?> createLecture(@RequestBody LectureDto lectureDto) {
        Lecture lecture = Lecture.builder()
                .module(moduleService.getModuleById(lectureDto.getModuleId()).get())
                .content(lectureDto.getContent())
                .title(lectureDto.getTitle())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Thêm bài giảng thành công!").data(lectureService.saveLecture(lecture)).build();
    }

    @PatchMapping("/{lectureId}")
    public Response<?> editLecture(@PathVariable("lectureId") Long lectureId, @RequestBody LectureDto lectureDto) {
        Optional<Lecture> optionalLecture = lectureService.getLectureById(lectureId);
        if (optionalLecture.isEmpty()) {
            throw new ResourceNotFoundException("Lecture with id " + lectureId + " not found!");
        }
        Lecture lecture = convertLectureDTO(lectureDto, optionalLecture);
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Sửa bài giảng thành công!").data(lectureService.saveLecture(lecture)).build();
    }

    @DeleteMapping("/{lectureId}")
    public Response<?> deleteLecture(@PathVariable("lectureId") Long lectureId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa bài giảng thành công!").data(lectureService.deleteLecture(lectureId)).build();
    }

    private Lecture convertLectureDTO(LectureDto lectureDto, Optional<Lecture> optionalLecture) {
        Lecture lecture = optionalLecture.get();
        if (lectureDto.getContent() != null) lecture.setContent(lectureDto.getContent());
        if(lectureDto.getTitle() != null) lecture.setTitle(lectureDto.getTitle());
        if (lectureDto.getModuleId() != null) lecture.setModule(moduleService.getModuleById(lectureDto.getModuleId()).get());
        return lecture;
    }

}
