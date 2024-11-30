package com.hcmute.utezbe.controller;

import com.hcmute.utezbe.dto.ResourcesDto;
import com.hcmute.utezbe.entity.Resources;
import com.hcmute.utezbe.exception.ResourceNotFoundException;
import com.hcmute.utezbe.request.CreateResourceRequest;
import com.hcmute.utezbe.response.Response;
import com.hcmute.utezbe.service.CloudinaryService;
import com.hcmute.utezbe.service.ModuleService;
import com.hcmute.utezbe.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final ModuleService moduleService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("")
    public Response<?> getAllResources() {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get all resources successfully!").data(resourcesService.getAllResources()).build();
    }

    @GetMapping("/{resourcesId}")
    public Response<?> getResourcesById(@PathVariable("resourcesId") Long resourcesId) {
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Get resources with id " + resourcesId + " successfully!").data(resourcesService.getResourcesById(resourcesId)).build();
    }

    @Transactional
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Response<?> createResources(@RequestPart("resources") CreateResourceRequest req, @RequestPart("file") MultipartFile document) {
        String urlDocument = cloudinaryService.upload(document);
        Resources resources = Resources.builder()
                .title(req.getTitle())
                .urlDocument(urlDocument)
                .module(moduleService.getModuleById(req.getModuleId()).get())
                .build();
        resourcesService.saveResources(resources);
        ResourcesDto resourcesDto = ResourcesDto.builder()
                .title(resources.getTitle())
                .urlDocument(resources.getUrlDocument())
                .moduleId(resources.getModule().getId())
                .build();
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Create resources successfully!").data(resourcesDto).build();
    }

    @PatchMapping("/{resourcesId}")
    public Response<?> editResources(@PathVariable("resourcesId") Long resourcesId,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestPart(value = "file", required = false) MultipartFile document) {
        Optional<Resources> optionalResources = resourcesService.getResourcesById(resourcesId);
        if (optionalResources.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Resources not found").build();
        }

        Resources resources = optionalResources.get();

        if (title != null) {
            resources.setTitle(title);
        }

        if (document != null) {
            String urlDocument = cloudinaryService.upload(document);
            resources.setUrlDocument(urlDocument);
        }

        resourcesService.saveResources(resources);

        ResourcesDto resourcesDto = ResourcesDto.builder()
                .title(resources.getTitle())
                .urlDocument(resources.getUrlDocument())
                .moduleId(resources.getModule().getId())
                .build();

        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Edit resources with id " + resourcesId + " successfully!").data(resourcesDto).build();
    }

    @DeleteMapping("/{resourcesId}")
    public Response<?> deleteResources(@PathVariable("resourcesId") Long resourcesId) {
        Optional<Resources> optionalResources = resourcesService.getResourcesById(resourcesId);
        if (optionalResources.isEmpty()) {
            throw new ResourceNotFoundException("Resources not found");
        }
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Delete resources with id " + resourcesId + " successfully!").data(resourcesService.deleteResources(resourcesId)).build();
    }

    private Resources convertResourcesDTO(ResourcesDto resourcesDto, Optional<Resources> optionalResources) {
        Resources resources = optionalResources.get();
        if (resourcesDto.getTitle() != null) resources.setTitle(resourcesDto.getTitle());
        if (resourcesDto.getUrlDocument() != null) resources.setUrlDocument(resourcesDto.getUrlDocument());
        if(resourcesDto.getModuleId() != null) resources.setModule(moduleService.getModuleById(resourcesDto.getModuleId()).get());
        return resources;
    }

}
