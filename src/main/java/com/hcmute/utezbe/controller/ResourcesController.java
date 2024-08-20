package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.ResourcesDto;
import com.hcmute.utezbe.entity.Resources;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.ModuleService;
import com.hcmute.utezbe.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final ModuleService moduleService;

    @GetMapping("")
    public Response getAllResources() {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all resources successfully!").data(resourcesService.getAllResources()).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{resourcesId}")
    public Response getResourcesById(@PathVariable("resourcesId") Long resourcesId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get resources with id " + resourcesId + " successfully!").data(resourcesService.getResourcesById(resourcesId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("")
    public Response createResources(@RequestBody ResourcesDto resourcesDto) {
        try{
            Resources resources = Resources.builder()
                    .module(moduleService.getModuleById(resourcesDto.getModuleId()).get())
                    .title(resourcesDto.getTitle())
                    .urlDocument(resourcesDto.getUrlDocument())
                    .build();
            return Response.builder().code(HttpStatus.CREATED.value()).success(true).message("Create resources successfully!").data(resourcesService.saveResources(resources)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PatchMapping("/{resourcesId}")
    public Response editResources(@PathVariable("resourcesId") Long resourcesId, @RequestBody ResourcesDto resourcesDto) {
        try{
            Optional<Resources> optionalResources = resourcesService.getResourcesById(resourcesId);
            Resources resources = optionalResources.get();
            resources = convertResourcesDTO(resourcesDto, optionalResources);
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit resources with id " + resourcesId + " successfully!").data(resourcesService.saveResources(resources)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{resourcesId}")
    public Response deleteResources(@PathVariable("resourcesId") Long resourcesId) {
        try {
            return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete resources with id " + resourcesId + " successfully!").data(resourcesService.deleteResources(resourcesId)).build();
        } catch (Exception e) {
            throw e;
        }
    }

    private Resources convertResourcesDTO(ResourcesDto resourcesDto, Optional<Resources> optionalResources) {
        Resources resources = optionalResources.get();
        if (resourcesDto.getTitle() != null) resources.setTitle(resourcesDto.getTitle());
        if (resourcesDto.getUrlDocument() != null) resources.setUrlDocument(resourcesDto.getUrlDocument());
        if(resourcesDto.getModuleId() != null) resources.setModule(moduleService.getModuleById(resourcesDto.getModuleId()).get());
        return resources;
    }

}
