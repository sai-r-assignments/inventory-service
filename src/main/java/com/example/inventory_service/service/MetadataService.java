package com.example.inventory_service.service;

import com.example.inventory_service.domain.InventoryDetails;
import com.example.inventory_service.domain.Location;
import com.example.inventory_service.domain.Metadata;
import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataService {

    private final MetadataRepository metadataRepository;

    private final LocationService locationService;

    public Collection<String> getAllDepartmentsWithLocation(String locationId) {
        Location location = locationService.getLocationById(locationId)
                .orElseThrow(() -> new DataNotFoundException("No data found with given location " + locationId));
        List<Metadata> metadata = metadataRepository.findByLocation(location);
        if (metadata.isEmpty()) {
            throw new DataNotFoundException("Metadata not found with given details");
        }
        return metadata.stream().map(Metadata::getInventoryDetails)
                .map(InventoryDetails::getDepartment).collect(toSet());
    }


    public Collection<String> getAllCategoriesWithLocationAndDepartment(
            String locationId, String departmentId) {
        Location location = locationService.getLocationById(locationId)
                .orElseThrow(() -> new DataNotFoundException("No data found with given location " + locationId));
        List<Metadata> metadata = metadataRepository.findByLocationAndInventoryDetails_Department(location, departmentId);
        if (metadata.isEmpty()) {
            throw new DataNotFoundException("Metadata not found with given details");
        }
        return metadata.stream().map(Metadata::getInventoryDetails)
                .map(InventoryDetails::getCategory).collect(toSet());
    }


    public Collection<String> getAllSubCategoriesWithLocationAndDepartmentAndCategory(
            String locationId, String departmentId, String categoryId) {
        Location location = locationService.getLocationById(locationId)
                .orElseThrow(() -> new DataNotFoundException("No data found with given location " + locationId));
        List<Metadata> metadata = metadataRepository
                .findByLocationAndInventoryDetails_DepartmentAndInventoryDetails_Category(location, departmentId, categoryId);
        if (metadata.isEmpty()) {
            throw new DataNotFoundException("Metadata not found with given details");
        }
        return metadata.stream().map(Metadata::getInventoryDetails)
                .map(InventoryDetails::getSubCategory).collect(toSet());
    }

    public Metadata getMetadataFromInventoryDetails(MetadataDto dto) {
        Location location = locationService.getLocationById(dto.getLocationId())
                .orElseThrow(() -> new DataNotFoundException("Data not found with given details"));
        return metadataRepository.findByLocationAndInventoryDetails(location, getInventoryDetails(dto))
                .orElseThrow(() -> new DataNotFoundException("Data not found with given details"));
    }

    public long getMetadataCount() {
        return metadataRepository.count();
    }


    public void addMetadata(MetadataDto dto) {
        Metadata saved = saveMetadata(dto);
        if(saved == null) {
            throw new InvalidOperationException("Metadata already exists with given details");
        }
    }

    public void deleteMetadata(MetadataDto dto) {
        Metadata metadata = getMetadataFromInventoryDetails(dto);
        try {
            metadataRepository.delete(metadata);
        } catch (DataIntegrityViolationException e) {
            log.error("Can not delete metadata as skudata exists in db referring to given data", e);
            throw new InvalidOperationException("Skudata exists referring to given metadata. Please delete skuData with given metadata.");
        }
    }

    @Transactional
    public Metadata saveMetadata(MetadataDto dto) {
        Location location = locationService.saveLocationIfNotExists(dto.getLocationId());
        return saveMetadata(dto, location);
    }

    private Metadata saveMetadata(MetadataDto dto, Location location) {
        Metadata metadata = getMetadataFromDto(dto);
        metadata.setLocation(location);
        try {
            metadataRepository.save(metadata);
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation while saving metadata with location {}," +
                            " dept {} category {} sub-category {}. Error: {}", dto.getLocationId(),
                    dto.getDepartmentId(), dto.getCategoryId(), dto.getSubCategoryId(), e.getMessage());
            return null;
        }
        return metadata;
    }

    private Metadata getMetadataFromDto(MetadataDto dto) {
        Metadata metadata = new Metadata();
        metadata.setInventoryDetails(getInventoryDetails(dto));
        return metadata;
    }

    private InventoryDetails getInventoryDetails(MetadataDto dto) {
        return new InventoryDetails(dto.getDepartmentId(),
                dto.getCategoryId(), dto.getSubCategoryId());
    }

}
