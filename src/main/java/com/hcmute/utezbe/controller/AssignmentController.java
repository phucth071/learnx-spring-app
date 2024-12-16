package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.AssignmentDto;
import com.hcmute.utezbe.entity.Assignment;
import com.hcmute.utezbe.entity.Course;
import com.hcmute.utezbe.entity.enumClass.State;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.request.CreateAssignmentRequest;
import com.hcmute.utezbe.response.AssignmentWithCourseId;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.AssignmentService;
import com.hcmute.utezbe.service.CloudinaryService;
import com.hcmute.utezbe.service.CourseService;
import com.hcmute.utezbe.service.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ModuleService moduleService;
    private final CloudinaryService cloudinaryService;
    private final CourseService courseService;

    @GetMapping("")
    public ResponseEntity<?> getAllAssignment() {
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment successfully!").data(assignmentService.getAllAssignments()).build());
    }

    @GetMapping("/pageable")
    public ResponseEntity<?> getAllAssignmentsPageable(Pageable pageable) {
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment pageable successfully!").data(assignmentService.getAllAssignmentsPageable(pageable)).build());
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("assignmentId") Long assignmentId) {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId).orElse(null);
        if (assignment == null) {
            throw new ResourceNotFoundException("Không tìm thấy bài tập!");
        }
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment with id " + assignmentId + " successfully!").data(assignmentService.getAssignmentById(assignmentId)).build());
    }

    @GetMapping("/get-by-user")
    public ResponseEntity<?> getAssignmentsByLoggedInUser() {
        List<Assignment> assignments = assignmentService.getAllAssignmentsLoggedInUser();
        List<AssignmentDto> assignmentDtos = assignments.stream().map(assignment -> AssignmentDto.builder()
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .build()).toList();
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignment by user successfully!").data(assignmentDtos).build());
    }

    @PostMapping(value = "", consumes = "multipart/form-data")
    public Response<?> createAssignment(@RequestPart("assignment") CreateAssignmentRequest req,
                                     @RequestPart(value = "document") @Nullable MultipartFile document) throws IOException, ParseException {
        String urlDocument = document != null ? cloudinaryService.uploadRemainFileName(document) : null;
        Assignment assignment = Assignment.builder()
                .content(req.getContent())
                .startDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(req.getStartDate()))
                .endDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(req.getEndDate()))
                .state(req.getState())
                .title(req.getTitle())
                .urlDocument(urlDocument)
                .module(moduleService.getModuleById(req.getModuleId()).get())
                .build();
        assignmentService.saveAssignment(assignment);
        AssignmentDto assignmentDto = AssignmentDto.builder()
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Tạo bài tập thành công!").data(assignmentDto).build();
    }

    @PatchMapping("/{assignmentId}")
    public Response<?> editAssignment(@PathVariable("assignmentId") Long assignmentId,
                                      @RequestParam("content") @Nullable String content,
                                      @RequestParam("startDate") @Nullable String startDate,
                                      @RequestParam("endDate") @Nullable String endDate,
                                      @RequestParam("state") @Nullable String state,
                                      @RequestParam("title") @Nullable String title,
                                      @RequestPart(value = "document", required = false) @Nullable MultipartFile document) throws ParseException {
        Optional<Assignment> optionalAssignment = assignmentService.getAssignmentById(assignmentId);
        if (optionalAssignment.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Không tìm thấy bài tập!").build();
        }
        String urlDocument = document != null ? cloudinaryService.upload(document) : null;
        Assignment assignment = optionalAssignment.get();
        if (content != null) assignment.setContent(content);
        if (startDate != null) assignment.setStartDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(startDate));
        if (endDate != null) assignment.setEndDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(endDate));
        if (state != null) assignment.setState(State.valueOf(state));
        if (title != null) assignment.setTitle(title);
        if (urlDocument != null) assignment.setUrlDocument(urlDocument);
        assignmentService.saveAssignment(assignment);
        AssignmentDto assignmentDto = AssignmentDto.builder()
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Chỉnh sửa bài tập thành công!").data(assignment).build();
    }

    @DeleteMapping("/{assignmentId}")
    public Response<?> deleteAssignment(@PathVariable("assignmentId") Long assignmentId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa bài tập thành công!").data(assignmentService.deleteAssignment(assignmentId)).build();
    }

    @GetMapping("/get-top-3")
    public ResponseEntity<?> getTop3AssignmentsByStudentId() {
        List<Assignment> assignments = assignmentService.getTop3AssignmentsByStudentId();
        List<AssignmentWithCourseId> assignmentDtos = convertToListAssignmentWithCourseId(assignments);
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get top 3 assignment by user successfully!").data(assignmentDtos).build());
    }

    @GetMapping("/get-by-month-year")
    public ResponseEntity<?> getAssignmentsByStudentIdAndEndDateMonthYear(@RequestParam("month") int month, @RequestParam("year") int year) {
        List<Assignment> assignments = assignmentService.getAllAssignmentsByStudentIdAndEndDateMonthYear(month, year);
        List<AssignmentWithCourseId> assignmentDtos = convertToListAssignmentWithCourseId(assignments);
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Lấy dữ liệu bài học thành công!").data(assignmentDtos).build());
    }

    private Assignment convertAssignmentDTO(AssignmentDto assignmentDto, Optional<Assignment> assignmentOptional) {
        Assignment assignment = assignmentOptional.get();
        if (assignmentDto.getContent() != null) assignment.setContent(assignmentDto.getContent());
        if (assignmentDto.getStartDate() != null) assignment.setStartDate(assignmentDto.getStartDate());
        if (assignmentDto.getState() != null) assignment.setState(assignmentDto.getState());
        if (assignmentDto.getEndDate() != null) assignment.setEndDate(assignmentDto.getEndDate());
        if (assignmentDto.getTitle() != null) assignment.setTitle(assignmentDto.getTitle());
        if (assignmentDto.getModuleId() != null) assignment.setModule(moduleService.getModuleById(assignmentDto.getModuleId()).get());
        return assignment;
    }

    @GetMapping("/get-by-next-x-day")
    public ResponseEntity<?> getAssignmentByNextXDay(@RequestParam("day") int day, @RequestParam("month") int month, @RequestParam("year") int year) {
        List<Assignment> assignments = assignmentService.getAssignmentByNextXDay(day, month, year);
        List<AssignmentWithCourseId> assignmentDtos = convertToListAssignmentWithCourseId(assignments);
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment by next 7 day successfully!").data(assignmentDtos).build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByKeyword(@RequestParam("keyword") String keyword) {
        List<Assignment> assignments = assignmentService.getAssignmentsByEmailAndTitleContaining(keyword);
        List<AssignmentWithCourseId> assignmentDtos = convertToListAssignmentWithCourseId(assignments);
        return ResponseEntity.ok(Response.builder().code(HttpStatus.OK.value()).success(true).message("Get assignment by keyword successfully!").data(assignmentDtos).build());
    }



    private List<AssignmentWithCourseId> convertToListAssignmentWithCourseId(List<Assignment> assignments) {
        return assignments.stream().map(assignment -> AssignmentWithCourseId.builder()
                .id(assignment.getId())
                .content(assignment.getContent())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .state(assignment.getState())
                .title(assignment.getTitle())
                .urlDocument(assignment.getUrlDocument())
                .moduleId(assignment.getModule().getId())
                .courseId(assignment.getModule().getCourse().getId())
                .courseName(assignment.getModule().getCourse().getName())
                .build()).toList();
    }
}
