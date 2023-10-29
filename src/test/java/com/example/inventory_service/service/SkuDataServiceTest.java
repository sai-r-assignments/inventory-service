package com.example.inventory_service.service;

import com.example.inventory_service.domain.SkuData;
import com.example.inventory_service.dto.SkuDataDto;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.repository.SkuDataRepository;
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
class SkuDataServiceTest {

    private static final SkuDataDto DTO = new SkuDataDto(SKU_1, SKU_1_DESC,
            LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);

    @Mock
    private SkuDataRepository skuDataRepository;

    @Mock
    private MetadataService metadataService;

    private SkuDataService skuDataService;

    @Captor
    private ArgumentCaptor<SkuData> skuDataArgumentCaptor;

    @BeforeEach
    void setUp() {
        skuDataService = new SkuDataService(skuDataRepository, metadataService);
    }

    @Test
    void test_get_sku_data() {
        when(metadataService.getMetadataFromInventoryDetails(DTO))
                .thenReturn(METADATA_1);
        when(skuDataRepository.findByMetadata(METADATA_1))
                .thenReturn(List.of(SKU_DATA_1, SKU_DATA_2));
        Collection<Long> skuList = skuDataService.getSkuDataWithMetadata(DTO);
        assertEquals(2, skuList.size());
        assertTrue(skuList.contains(SKU_1));
        assertTrue(skuList.contains(SKU_2));
        verify(metadataService).getMetadataFromInventoryDetails(DTO);
        verify(skuDataRepository).findByMetadata(METADATA_1);
        verifyNoMoreInteractions(metadataService, skuDataRepository);
    }

    @Test
    void test_get_sku_data_throws_DataNotFoundException_when_metadata_not_found() {
        when(metadataService.getMetadataFromInventoryDetails(DTO))
                .thenThrow(new DataNotFoundException("DNF"));
        Exception e = assertThrows(DataNotFoundException.class,
                () -> skuDataService.getSkuDataWithMetadata(DTO));
        assertTrue(e.getMessage().contains("Skudata not found with given details"));
        verify(metadataService).getMetadataFromInventoryDetails(DTO);
        verifyNoMoreInteractions(metadataService);
        verifyNoInteractions(skuDataRepository);
    }

    @Test
    void test_get_sku_data_throws_DataNotFoundException_when_skudata_not_found() {
        when(metadataService.getMetadataFromInventoryDetails(DTO))
                .thenReturn(METADATA_1);
        when(skuDataRepository.findByMetadata(METADATA_1))
                .thenReturn(Collections.emptyList());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> skuDataService.getSkuDataWithMetadata(DTO));
        assertTrue(e.getMessage().contains("Skudata not found with given details"));
        verify(metadataService).getMetadataFromInventoryDetails(DTO);
        verify(skuDataRepository).findByMetadata(METADATA_1);
        verifyNoMoreInteractions(metadataService, skuDataRepository);
    }

    @Test
    void test_get_skudata_count() {
        when(skuDataRepository.count())
                .thenReturn(10L);
        assertEquals(10L, skuDataService.getSkudataCount());
        verifyNoMoreInteractions(skuDataRepository);
        verifyNoInteractions(metadataService);
    }

    @Test
    void test_save_sku_data_metadata_exists() {
        when(metadataService.getMetadataFromInventoryDetails(DTO))
                .thenReturn(METADATA_1);
        skuDataService.saveSkudata(DTO, true);
        verify(metadataService).getMetadataFromInventoryDetails(DTO);
        verify(skuDataRepository).save(skuDataArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataService, skuDataRepository);
        SkuData savedSku = skuDataArgumentCaptor.getValue();
        assertEquals(DTO.getSku(), savedSku.getSku());
        assertEquals(DTO.getName(), savedSku.getName());
        assertEquals(METADATA_1, savedSku.getMetadata());
    }

    @Test
    void test_save_sku_data_metadata_not_exists_throw_error_true_throws_DataNotFoundException() {
        when(metadataService.getMetadataFromInventoryDetails(DTO))
                .thenThrow(new DataNotFoundException("DNF"));
        Exception e = assertThrows(DataNotFoundException.class,
                () -> skuDataService.saveSkudata(DTO, false));
        assertEquals("DNF", e.getMessage());
        verify(metadataService).getMetadataFromInventoryDetails(DTO);
        verifyNoMoreInteractions(metadataService);
        verifyNoInteractions(skuDataRepository);
    }

    @Test
    void test_save_sku_data_metadata_not_exists_throw_error_false_saves_metadata() {
        when(metadataService.getMetadataFromInventoryDetails(DTO))
                .thenThrow(new DataNotFoundException("DNF"));
        when(metadataService.saveMetadata(DTO))
                .thenReturn(METADATA_1);
        skuDataService.saveSkudata(DTO, true);
        verify(metadataService).getMetadataFromInventoryDetails(DTO);
        verify(metadataService).saveMetadata(DTO);
        verify(skuDataRepository).save(skuDataArgumentCaptor.capture());
        verifyNoMoreInteractions(metadataService, skuDataRepository);
        SkuData savedSku = skuDataArgumentCaptor.getValue();
        assertEquals(DTO.getSku(), savedSku.getSku());
        assertEquals(DTO.getName(), savedSku.getName());
        assertEquals(METADATA_1, savedSku.getMetadata());
    }


    @Test
    void test_delete_skuData() {
        when(skuDataRepository.findById(SKU_1))
                .thenReturn(Optional.of(SKU_DATA_1));
        skuDataService.deleteSkudata(SKU_1);
        verify(skuDataRepository).findById(SKU_1);
        verify(skuDataRepository).delete(SKU_DATA_1);
        verifyNoMoreInteractions(skuDataRepository);
    }

    @Test
    void test_delete_skuData_throws_DataNotFoundException_when_given_sku_not_found() {
        when(skuDataRepository.findById(12345L))
                .thenReturn(Optional.empty());
        Exception e = assertThrows(DataNotFoundException.class,
                () -> skuDataService.deleteSkudata(12345L));
        assertTrue(e.getMessage().contains(String.valueOf(12345)));
        verify(skuDataRepository).findById(12345L);
        verifyNoMoreInteractions(skuDataRepository);
    }
}
