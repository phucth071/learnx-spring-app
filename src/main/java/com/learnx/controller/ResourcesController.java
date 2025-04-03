package com.learnx.controller;

import com.learnx.dto.ResourcesDto;
import com.learnx.entity.Resources;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.request.CreateResourceRequest;
import com.learnx.response.Response;
import com.learnx.service.CloudinaryService;
import com.learnx.service.ModuleService;
import com.learnx.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public Response<?> createResources(@RequestPart("resources") CreateResourceRequest req, @RequestPart("document") @Nullable MultipartFile document) throws IOException {
        assert document != null;
        String urlDocument = cloudinaryService.uploadRemainFileName(document);
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
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Tạo tài nguyên thành công!").data(resourcesDto).build();
    }

    @PatchMapping("/{resourcesId}")
    public Response<?> editResources(@PathVariable("resourcesId") Long resourcesId,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestPart(value = "file", required = false) MultipartFile document) throws IOException {
        Optional<Resources> optionalResources = resourcesService.getResourcesById(resourcesId);
        if (optionalResources.isEmpty()) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).success(false).message("Không tìm thấy tài nguyên!").build();
        }

        Resources resources = optionalResources.get();

        if (title != null) {
            resources.setTitle(title);
        }

        if (document != null) {
            String urlDocument = cloudinaryService.uploadRemainFileName(document);
            resources.setUrlDocument(urlDocument);
        }

        resourcesService.saveResources(resources);

        ResourcesDto resourcesDto = ResourcesDto.builder()
                .title(resources.getTitle())
                .urlDocument(resources.getUrlDocument())
                .moduleId(resources.getModule().getId())
                .build();

        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Sửa tài nguyên thành công!").data(resourcesDto).build();
    }

    @DeleteMapping("/{resourcesId}")
    public Response<?> deleteResources(@PathVariable("resourcesId") Long resourcesId) {
        Optional<Resources> optionalResources = resourcesService.getResourcesById(resourcesId);
        if (optionalResources.isEmpty()) {
            throw new ResourceNotFoundException("Resources not found");
        }
        return Response.builder().code(HttpStatus.OK.value()).success(true).message("Xóa tài nguyên thành công!").data(resourcesService.deleteResources(resourcesId)).build();
    }

    private Resources convertResourcesDTO(ResourcesDto resourcesDto, Optional<Resources> optionalResources) {
        Resources resources = optionalResources.get();
        if (resourcesDto.getTitle() != null) resources.setTitle(resourcesDto.getTitle());
        if (resourcesDto.getUrlDocument() != null) resources.setUrlDocument(resourcesDto.getUrlDocument());
        if(resourcesDto.getModuleId() != null) resources.setModule(moduleService.getModuleById(resourcesDto.getModuleId()).get());
        return resources;
    }

}
