package com.example.inventory_service.controller;

import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.dto.SkuDataDto;
import com.example.inventory_service.service.SkuDataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/skuData")
@RequiredArgsConstructor
@Slf4j
public class SkuDataController {

    private final SkuDataService skuDataService;

    @GetMapping
    public Collection<Long> getSkuData(@RequestParam @NotBlank String locationId,
                                       @RequestParam @NotBlank String departmentId,
                                       @RequestParam @NotBlank String categoryId,
                                       @RequestParam @NotBlank String subCategoryId) {
        log.info("Received get sku data service call with location {}, department {}, category {}, sub-category {}",
                locationId, departmentId, categoryId, subCategoryId);
        MetadataDto dto = new MetadataDto(locationId, departmentId, categoryId, subCategoryId);
        return skuDataService.getSkuDataWithMetadata(dto);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void createSkuData(@RequestBody @Valid SkuDataDto skuDataDto) {
        log.info("Received add sku data service call with skuData {}", skuDataDto);
        skuDataService.saveSkudata(skuDataDto, false);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteSkuData(@RequestParam @Min(1) Long sku) {
        log.info("Received delete sku data service call with sku {}", sku);
        skuDataService.deleteSkudata(sku);
    }
}
