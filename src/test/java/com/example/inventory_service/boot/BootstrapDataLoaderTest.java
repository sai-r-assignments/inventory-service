package com.example.inventory_service.boot;

import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.dto.SkuDataDto;
import com.example.inventory_service.service.MetadataService;
import com.example.inventory_service.service.SkuDataService;
import com.example.inventory_service.utils.CsvReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.List;

import static com.example.inventory_service.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class BootstrapDataLoaderTest {

    private static final String METADATA_FILE = "metadata.file";
    private static final String SKUDATA_FILE = "skudata.file";
    @Mock
    private MetadataService metadataService;

    @Mock
    private SkuDataService skuDataService;

    @Mock
    private ApplicationReadyEvent applicationReadyEvent;

    @Captor
    private ArgumentCaptor<MetadataDto> metadataDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<SkuDataDto> skuDataDtoArgumentCaptor;

    private BootstrapDataLoader bootstrapDataLoader;

    @BeforeEach
    void setUp() {
        bootstrapDataLoader = new BootstrapDataLoader(metadataService, skuDataService);
        setField(bootstrapDataLoader, "metadataCsvFile", METADATA_FILE);
        setField(bootstrapDataLoader, "skuDataCsvFile", SKUDATA_FILE);
        setField(bootstrapDataLoader, "saveNewMetadataFromSkuData", true);
    }

    @Test
    void test_load_metadata() {
        try (MockedStatic<CsvReader> csvReader = Mockito.mockStatic(CsvReader.class)) {
            csvReader.when(() -> CsvReader.loadCSVData(METADATA_FILE))
                    .thenReturn(getSampleMetadata());
            csvReader.when(() -> CsvReader.loadCSVData(SKUDATA_FILE))
                    .thenReturn(getSampleSkuData());
            when(metadataService.getMetadataCount())
                    .thenReturn(2L).thenReturn(3L);
            when(skuDataService.getSkudataCount()).thenReturn(1L);
            bootstrapDataLoader.onApplicationEvent(applicationReadyEvent);
            verify(metadataService, times(2))
                    .saveMetadata(metadataDtoArgumentCaptor.capture());
            verify(skuDataService).saveSkudata(skuDataDtoArgumentCaptor.capture(), eq(true));
            verify(metadataService, times(2)).getMetadataCount();
            verify(skuDataService).getSkudataCount();
            verifyNoMoreInteractions(metadataService, skuDataService);
            List<MetadataDto> metadataDtoList = metadataDtoArgumentCaptor.getAllValues();
            verifyMetadataDTo(metadataDtoList.get(0), LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
            verifyMetadataDTo(metadataDtoList.get(1), LOCATION_ID_2, DEPARTMENT_2, CATEGORY_2, SUB_CATEGORY_2);
            verifySkudataDTo(skuDataDtoArgumentCaptor.getValue(), SKU_1, SKU_1_DESC,
                    LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1);
            csvReader.verify(() -> CsvReader.loadCSVData(METADATA_FILE));
            csvReader.verify(() -> CsvReader.loadCSVData(SKUDATA_FILE));
            csvReader.verifyNoMoreInteractions();
        }
    }

    private void verifySkudataDTo(SkuDataDto dto, Long sku, String name, String location, String dept,
                                  String category, String subCategory) {
        assertEquals(sku, dto.getSku());
        assertEquals(name, dto.getName());
        verifyMetadataDTo(dto, location, dept, category, subCategory);
    }

    private void verifyMetadataDTo(MetadataDto dto, String location, String dept,
                                   String category, String subCategory) {
        assertEquals(location, dto.getLocationId());
        assertEquals(dept, dto.getDepartmentId());
        assertEquals(category, dto.getCategoryId());
        assertEquals(subCategory, dto.getSubCategoryId());
    }

    private List<String[]> getSampleMetadata() {
        return List.of(
                new String[]{LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1},
                new String[]{LOCATION_ID_2, DEPARTMENT_2, CATEGORY_2, SUB_CATEGORY_2}
        );
    }

    private List<String[]> getSampleSkuData() {
        return List.of(
                new String[]{String.valueOf(SKU_1), SKU_1_DESC, LOCATION_ID_1, DEPARTMENT_1, CATEGORY_1, SUB_CATEGORY_1},
                new String[]{"bla", SKU_2_DESC, LOCATION_ID_2, DEPARTMENT_2, CATEGORY_2, SUB_CATEGORY_2}
        );
    }
}
