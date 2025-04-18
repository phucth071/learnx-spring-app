package com.learnx.controller;

import com.learnx.dto.ModuleDto;
import com.learnx.entity.Module;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.request.UpdateModuleRequest;
import com.learnx.response.Response;
import com.learnx.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseService courseService;
    private final LectureService lectureService;
    private final ResourcesService resourcesService;
    private final AssignmentService assignmentService;

    @GetMapping("")
    public Response<?> getAllModule() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all module successfully!").data(moduleService.getAllModules()).build();
    }

    @GetMapping("/{moduleId}")
    public Response<?> getModuleById(@PathVariable("moduleId") Long moduleId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get module with id " + moduleId + " successfully!").data(moduleService.getModuleById(moduleId)).build();
    }

    @PostMapping("")
    public Response<?> createModule(@RequestBody ModuleDto moduleDto) {
        Module module = Module.builder()
                .name(moduleDto.getName())
                .description(moduleDto.getDescription() != null ? moduleDto.getDescription() : "")
                .course(courseService.getCourseById(moduleDto.getCourseId()).get())
                .build();
        return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Thêm chương thành công!").data(moduleService.saveModule(module)).build();
    }

    @PatchMapping("/{moduleId}")
    public Response<?> editModule(@PathVariable("moduleId") Long moduleId, @RequestBody @Nullable UpdateModuleRequest req) {
        Optional<Module> moduleOptional = moduleService.getModuleById(moduleId);
        if (moduleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Module with id " + moduleId + " not found!");
        }
        Module module = moduleOptional.get();
        assert req != null;
        if (req.getName() != null) module.setName(req.getName());
        if (req.getDescription() != null) module.setDescription(req.getDescription());
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Sửa chương thành công!").data(moduleService.saveModule(module)).build();
    }

    @DeleteMapping("/{moduleId}")
    public Response<?> deleteModule(@PathVariable("moduleId") Long moduleId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa chương thành công!").data(moduleService.deleteModule(moduleId)).build();
    }

    @GetMapping("/{moduleId}/lectures")
    public Response<?> getLecturesByModuleId(@PathVariable("moduleId") Long moduleId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all lectures of module with id " + moduleId + " successfully!").data(lectureService.getAllLecturesByModuleId(moduleId)).build();
    }

    @GetMapping("/{moduleId}/resources")
    public Response<?> getResourcesByModuleId(@PathVariable("moduleId") Long moduleId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all resources of module with id " + moduleId + " successfully!").data(resourcesService.getAllResourcesByModuleId(moduleId)).build();
    }

    @GetMapping("/{moduleId}/assignments")
    public Response<?> getAssignmentsByModuleId(@PathVariable("moduleId") Long moduleId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all assignments of module with id " + moduleId + " successfully!").data(assignmentService.getAllAssignmentsByModuleId(moduleId)).build();
    }

    private Module convertModuleDTO(ModuleDto moduleDto, Optional<Module> moduleOptional) {
        Module module = moduleOptional.get();
        if (module.getName() != null) module.setName(moduleDto.getName());
        if (module.getDescription() != null) module.setDescription(moduleDto.getDescription());
        if (module.getCourse() != null) module.setCourse(courseService.getCourseById(moduleDto.getCourseId()).get());
        return module;
    }

}
