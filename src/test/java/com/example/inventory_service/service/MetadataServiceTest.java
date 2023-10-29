package com.example.inventory_service.service;

import com.example.inventory_service.domain.InventoryDetails;
import com.example.inventory_service.domain.Metadata;
import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.repository.MetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.inventory_service.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetadataServiceTest {

    private static final MetadataDto DTO = new MetadataDto(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);

    @Mock
    private LocationService locationService;

    @Mock
    private MetadataRepository metadataRepository;

    @Captor
    private ArgumentCaptor<Metadata> metadataArgumentCaptor;

    @Captor
    private ArgumentCaptor<InventoryDetails> inventoryDetailsArgumentCaptor;

    private MetadataService metadataService;

    @BeforeEach
    void setUp() {
        metadataService = new MetadataService(metadataRepository, locationService);
    }

    @Test
    void test_get_all_departments_with_given_location() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocation(LOCATION_1))
                .thenReturn(METADATA_LOC_1);
        Collection<String> depts = metadataService.getAllDepartmentsWithLocation(LOCATION_ID_1);
        assertEquals(2, depts.size());
        assertTrue(depts.contains(DEPARTMENT_1));
        assertTrue(depts.contains(DEPARTMENT_2));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocation(LOCATION_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
    }

    @Test
    void test_get_all_departments_with_given_location_throws_DataNotFoundException_when_location_not_exists() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService.getAllDepartmentsWithLocation(LOCATION_ID_1));
        assertTrue(e.getMessage().contains(LOCATION_ID_1));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verifyNoMoreInteractions(locationService);
        verifyNoInteractions(metadataRepository);
    }

    @Test
    void test_get_all_departments_with_given_location_throws_DataNotFoundException_when_metadata_not_exists() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocation(LOCATION_1))
                .thenReturn(Collections.emptyList());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService.getAllDepartmentsWithLocation(LOCATION_ID_1));
        assertTrue(e.getMessage().contains("Metadata not found with given details"));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocation(LOCATION_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
    }

    @Test
    void test_get_all_categories_with_given_location_department() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails_Department(LOCATION_1, DEPARTMENT_1))
                .thenReturn(List.of(METADATA_1, METADATA_2, METADATA_3));
        Collection<String> categories = metadataService
                .getAllCategoriesWithLocationAndDepartment(LOCATION_ID_1, DEPARTMENT_1);
        assertEquals(2, categories.size());
        assertTrue(categories.contains(CATEGORY_1));
        assertTrue(categories.contains(CATEGORY_2));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails_Department(LOCATION_1, DEPARTMENT_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
    }

    @Test
    void test_get_all_categories_with_given_location_department_throws_DataNotFoundException_when_location_not_exists() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService.getAllCategoriesWithLocationAndDepartment(LOCATION_ID_1, DEPARTMENT_1));
        assertTrue(e.getMessage().contains(LOCATION_ID_1));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verifyNoMoreInteractions(locationService);
        verifyNoInteractions(metadataRepository);
    }

    @Test
    void test_get_all_categories_with_given_location_department_throws_DataNotFoundException_when_metadata_not_exists() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails_Department(LOCATION_1, DEPARTMENT_1))
                .thenReturn(Collections.emptyList());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService.getAllCategoriesWithLocationAndDepartment(LOCATION_ID_1, DEPARTMENT_1));
        assertTrue(e.getMessage().contains("Metadata not found with given details"));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails_Department(LOCATION_1, DEPARTMENT_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
    }

    @Test
    void test_get_all_subcategories_with_given_location_department_category() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails_DepartmentAndInventoryDetails_Category(
                LOCATION_1, DEPARTMENT_1, CATEGORY_1)).thenReturn(List.of(METADATA_1, METADATA_2));
        Collection<String> subCategories = metadataService
                .getAllSubCategoriesWithLocationAndDepartmentAndCategory(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1);
        assertEquals(2, subCategories.size());
        assertTrue(subCategories.contains(SUB_CATEGORY_1));
        assertTrue(subCategories.contains(SUB_CATEGORY_2));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails_DepartmentAndInventoryDetails_Category(
                LOCATION_1, DEPARTMENT_1, CATEGORY_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
    }

    @Test
    void test_get_all_subcategories_with_given_location_department_category_throws_DataNotFoundException_when_location_not_exists() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService
                        .getAllSubCategoriesWithLocationAndDepartmentAndCategory(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1));
        assertTrue(e.getMessage().contains(LOCATION_ID_1));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verifyNoMoreInteractions(locationService);
        verifyNoInteractions(metadataRepository);
    }

    @Test
    void test_get_all_subcategories_with_given_location_department_category_throws_DataNotFoundException_when_metadata_not_exists() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails_DepartmentAndInventoryDetails_Category(
                LOCATION_1, DEPARTMENT_1, CATEGORY_1)).thenReturn(Collections.emptyList());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService
                        .getAllSubCategoriesWithLocationAndDepartmentAndCategory(LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1));
        assertTrue(e.getMessage().contains("Metadata not found with given details"));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails_DepartmentAndInventoryDetails_Category(
                LOCATION_1, DEPARTMENT_1, CATEGORY_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
    }

    @Test
    void test_get_metadata_from_all_inventory_details() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails(eq(LOCATION_1), any(InventoryDetails.class)))
                .thenReturn(Optional.of(METADATA_1));
        Metadata metadata = metadataService.getMetadataFromInventoryDetails(DTO);
        assertEquals(METADATA_1, metadata);
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails(
                eq(LOCATION_1), inventoryDetailsArgumentCaptor.capture());
        verifyNoMoreInteractions(locationService, metadataRepository);
        verifyInventoryDetails(inventoryDetailsArgumentCaptor.getValue(),
                DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    }

    @Test
    void test_get_metadata_from_all_inventory_details_throws_DataNotFoundException_when_location_not_found() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService.getMetadataFromInventoryDetails(DTO));
        assertTrue(e.getMessage().contains("Data not found with given details"));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verifyNoMoreInteractions(locationService);
        verifyNoInteractions(metadataRepository);
    }

    @Test
    void test_get_metadata_from_all_inventory_details_throws_DataNotFoundException_when_metadata_not_found() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails(eq(LOCATION_1), any(InventoryDetails.class)))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> metadataService.getMetadataFromInventoryDetails(DTO));
        assertTrue(e.getMessage().contains("Data not found with given details"));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails(
                eq(LOCATION_1), inventoryDetailsArgumentCaptor.capture());
        verifyNoMoreInteractions(locationService, metadataRepository);
        verifyInventoryDetails(inventoryDetailsArgumentCaptor.getValue(),
                DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    }

    @Test
    void test_get_metadata_count() {
        when(metadataRepository.count())
                .thenReturn(10L);
        assertEquals(10L, metadataService.getMetadataCount());
        verifyNoMoreInteractions(metadataRepository);
        verifyNoInteractions(locationService);
    }

    @Test
    void add_new_metadata() {
        when(locationService.saveLocationIfNotExists(LOCATION_ID_1))
                .thenReturn(LOCATION_1);
        metadataService.addMetadata(DTO);
        verify(locationService).saveLocationIfNotExists(LOCATION_ID_1);
        verifyNoMoreInteractions(locationService);
        verify(metadataRepository).save(metadataArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataRepository);
        assertEquals(LOCATION_1, metadataArgumentCaptor.getValue().getLocation());
        verifyInventoryDetails(metadataArgumentCaptor.getValue().getInventoryDetails(),
                DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    }

    @Test
    void add_new_metadata_throws_InvalidOperationException_when_metadata_exists() {
        when(locationService.saveLocationIfNotExists(LOCATION_ID_1))
                .thenReturn(LOCATION_1);
        doThrow(new DataIntegrityViolationException("DIVE"))
                .when(metadataRepository).save(any(Metadata.class));
        assertThrows(InvalidOperationException.class,
                () -> metadataService.addMetadata(DTO));
        verify(locationService).saveLocationIfNotExists(LOCATION_ID_1);
        verifyNoMoreInteractions(locationService);
        verify(metadataRepository).save(metadataArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataRepository);
        assertEquals(LOCATION_1, metadataArgumentCaptor.getValue().getLocation());
        verifyInventoryDetails(metadataArgumentCaptor.getValue().getInventoryDetails(),
                DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    }

    @Test
    void delete_metadata() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails(eq(LOCATION_1), any(InventoryDetails.class)))
                .thenReturn(Optional.of(METADATA_1));
        metadataService.deleteMetadata(DTO);
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails(
                eq(LOCATION_1), inventoryDetailsArgumentCaptor.capture());
        verify(metadataRepository).delete(METADATA_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
        verifyInventoryDetails(inventoryDetailsArgumentCaptor.getValue(),
                DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    }

    @Test
    void delete_metadata_throws_InvalidOperationException_when_integrity_constraint_violated() {
        when(locationService.getLocationById(LOCATION_ID_1))
                .thenReturn(Optional.of(LOCATION_1));
        when(metadataRepository.findByLocationAndInventoryDetails(eq(LOCATION_1), any(InventoryDetails.class)))
                .thenReturn(Optional.of(METADATA_1));
        doThrow(new DataIntegrityViolationException("DIVE"))
                .when(metadataRepository).delete(METADATA_1);
        assertThrows(InvalidOperationException.class,
                () -> metadataService.deleteMetadata(DTO));
        verify(locationService).getLocationById(LOCATION_ID_1);
        verify(metadataRepository).findByLocationAndInventoryDetails(
                eq(LOCATION_1), inventoryDetailsArgumentCaptor.capture());
        verify(metadataRepository).delete(METADATA_1);
        verifyNoMoreInteractions(locationService, metadataRepository);
        verifyInventoryDetails(inventoryDetailsArgumentCaptor.getValue(),
                DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
    }

    private void verifyInventoryDetails(InventoryDetails inventoryDetails,
                                        String department, String category, String subCategory) {
        assertEquals(department, inventoryDetails.getDepartment());
        assertEquals(category, inventoryDetails.getCategory());
        assertEquals(subCategory, inventoryDetails.getSubCategory());
    }
}
