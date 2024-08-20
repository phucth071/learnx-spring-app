package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.LectureDto;
import com.hcmute.utezbe.entity.Lecture;
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
    public Response getAllLecture() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all lecture successfully!").data(lectureService.getAllLectures()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{lectureId}")
    public Response getLectureById(@PathVariable("lectureId") Long lectureId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get lecture with id " + lectureId + " successfully!").data(lectureService.getLectureById(lectureId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createLecture(@RequestBody LectureDto lectureDto) {
        try{
            Lecture lecture = Lecture.builder()
                    .module(moduleService.getModuleById(lectureDto.getModuleId()).get())
                    .content(lectureDto.getContent())
                    .name(lectureDto.getName())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create lecture successfully!").data(lectureService.saveLecture(lecture)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{lectureId}")
    public Response editLecture(@PathVariable("lectureId") Long lectureId, @RequestBody LectureDto lectureDto) {
        try{
            Optional<Lecture> optionalLecture = lectureService.getLectureById(lectureId);
            Lecture lecture = optionalLecture.get();
            lecture = convertLectureDTO(lectureDto, optionalLecture);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit lecture with id " + lectureId + " successfully!").data(lectureService.saveLecture(lecture)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{lectureId}")
    public Response deleteLecture(@PathVariable("lectureId") Long lectureId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete lecture with id " + lectureId + " successfully!").data(lectureService.deleteLecture(lectureId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private Lecture convertLectureDTO(LectureDto lectureDto, Optional<Lecture> optionalLecture) {
        Lecture lecture = optionalLecture.get();
        if (lectureDto.getContent() != null) lecture.setContent(lectureDto.getContent());
        if(lectureDto.getName() != null) lecture.setName(lectureDto.getName());
        if (lectureDto.getModuleId() != null) lecture.setModule(moduleService.getModuleById(lectureDto.getModuleId()).get());
        return lecture;
    }

}
