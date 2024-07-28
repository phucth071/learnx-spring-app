package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.ModuleDto;
import com.hcmute.utezbe.entity.Module;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CourseService;
import com.hcmute.utezbe.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseService courseService;

    @GetMapping("")
    public Response getAllModule() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all module successfully!").data(moduleService.getAllModules()).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get all module failed!").data(null).build();
        }
    }

    @GetMapping("/{moduleId}")
    public Response getModuleById(@PathVariable("moduleId") Long moduleId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get module with id " + moduleId + " successfully!").data(moduleService.getModuleById(moduleId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Get module with id " + moduleId + " failed!").data(null).build();
        }
    }

    @PostMapping("")
    public Response createModule(@RequestBody ModuleDto moduleDto) {
        try {
            Module module = Module.builder()
                    .name(moduleDto.getName())
                    .description(moduleDto.getDescription())
                    .course(courseService.getCourseById(moduleDto.getCourseId()).get())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create module successfully!").data(moduleService.saveModule(module)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Create module failed!").data(null).build();
        }
    }

    @PatchMapping("/{moduleId}")
    public Response editModule(@PathVariable("moduleId") Long moduleId, @RequestBody ModuleDto moduleDto) {
        try {
            Optional<Module> moduleOptional = moduleService.getModuleById(moduleId);
            if (!moduleOptional.isPresent()) {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Module with id " + moduleId + " not found!").data(null).build();
            }
            Module module = moduleOptional.get();
            if (module != null) {
                module = convertModuleDTO(moduleDto, moduleOptional);
                return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit module with id " + moduleId + " successfully!").data(moduleService.saveModule(module)).build();
            } else {
                return Response.builder().code(HttpStatus.OK.value()).success(false).message("Module with id " + moduleId + " not found!").data(null).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Edit module with id " + moduleId + " failed!").data(null).build();
        }
    }

    @DeleteMapping("/{moduleId}")
    public Response deleteModule(@PathVariable("moduleId") Long moduleId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete module with id " + moduleId + " successfully!").data(moduleService.deleteModule(moduleId)).build();
        } catch (Exception e) {
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).message("Delete module with id " + moduleId + " failed!").data(null).build();
        }
    }

    private Module convertModuleDTO(ModuleDto moduleDto, Optional<Module> moduleOptional) {
        Module module = moduleOptional.get();
        if (module.getName() != null) module.setName(moduleDto.getName());
        if (module.getDescription() != null) module.setDescription(moduleDto.getDescription());
        if (module.getCourse() != null) module.setCourse(courseService.getCourseById(moduleDto.getCourseId()).get());
        return module;
    }

}
