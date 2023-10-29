package com.example.inventory_service.controller;

import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.service.MetadataService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
@Slf4j
public class MetadataController {

    private final MetadataService metadataService;

    @GetMapping("/{location_id}/department")
    public Collection<String> getAllDepartments(
            @PathVariable("location_id") @NotBlank String locationId) {
        log.info("Received request to get all departments with location {}", locationId);
        return metadataService.getAllDepartmentsWithLocation(locationId);
    }

    @GetMapping("/{location_id}/department/{department_id}/category")
    public Collection<String> getAllCategories(
            @PathVariable("location_id") @NotBlank String locationId,
            @PathVariable("department_id") @NotBlank String departmentId) {
        log.info("Received request to get all categories with location {}, department {}",
                locationId, departmentId);
        return metadataService.getAllCategoriesWithLocationAndDepartment(locationId, departmentId);
    }

    @GetMapping("/{location_id}/department/{department_id}/category/{category_id}/subcategory")
    public Collection<String> getSubCategories(
            @PathVariable("location_id") @NotBlank String locationId,
            @PathVariable("department_id") @NotBlank String departmentId,
            @PathVariable("category_id") @NotBlank String categoryId) {
        log.info("Received request to get sub categories with location {}, department {}, category {},",
                locationId, departmentId, categoryId);
        return metadataService.getAllSubCategoriesWithLocationAndDepartmentAndCategory(
                locationId, departmentId, categoryId);
    }
    @GetMapping("/{location_id}/department/{department_id}/category/{category_id}/" +
            "subcategory/{subcategory_id}")
    public MetadataDto getMetadata(
            @PathVariable("location_id") @NotBlank String locationId,
            @PathVariable("department_id") @NotBlank String departmentId,
            @PathVariable("category_id") @NotBlank String categoryId,
            @PathVariable("subcategory_id") @NotBlank String subCategoryId) {
        log.info("Received request to get metadata with location {}, department {}, category {}, sub-category {}",
                locationId, departmentId, categoryId, subCategoryId);
        MetadataDto dto = new MetadataDto(locationId, departmentId, categoryId, subCategoryId);
        metadataService.getMetadataFromInventoryDetails(dto);
        return dto;
    }

    @PostMapping("/{location_id}/department/{department_id}/category/{category_id}/" +
            "subcategory/{subcategory_id}")
    @ResponseStatus(CREATED)
    public void addMetadata(
            @PathVariable("location_id") @NotBlank String locationId,
            @PathVariable("department_id") @NotBlank String departmentId,
            @PathVariable("category_id") @NotBlank String categoryId,
            @PathVariable("subcategory_id") @NotBlank String subCategoryId) {
        log.info("Received request to add metadata with location {}, department {}, category {}, sub-category {}",
                locationId, departmentId, categoryId, subCategoryId);
        MetadataDto dto = new MetadataDto(locationId, departmentId, categoryId, subCategoryId);
        metadataService.addMetadata(dto);
    }

    @DeleteMapping("/{location_id}/department/{department_id}/category/{category_id}/" +
            "subcategory/{subcategory_id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteMetadata(
            @PathVariable("location_id") @NotBlank String locationId,
            @PathVariable("department_id") @NotBlank String departmentId,
            @PathVariable("category_id") @NotBlank String categoryId,
            @PathVariable("subcategory_id") @NotBlank String subCategoryId) {
        log.info("Received request to delete metadata with location {}, department {}, category {}, sub-category {}",
                locationId, departmentId, categoryId, subCategoryId);
        MetadataDto dto = new MetadataDto(locationId, departmentId, categoryId, subCategoryId);
        metadataService.deleteMetadata(dto);
    }
}
