package com.example.inventory_service.boot;

import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.dto.SkuDataDto;
import com.example.inventory_service.service.MetadataService;
import com.example.inventory_service.service.SkuDataService;
import com.example.inventory_service.utils.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootstrapDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final MetadataService metadataService;

    private final SkuDataService skuDataService;

    @Value("${metadata.csv.file}")
    private String metadataCsvFile;

    @Value("${skuData.csv.file}")
    private String skuDataCsvFile;

    @Value("${skuData.load.metadata.save}")
    private boolean saveNewMetadataFromSkuData;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        loadAndSaveMetadata(metadataCsvFile);
        loadAndSaveSkudata(skuDataCsvFile);
    }

    private void loadAndSaveMetadata(String csvFilePath) {
        log.info("Loading metadata from {}", csvFilePath);
        List<String[]> metadataFromCsv = CsvReader.loadCSVData(csvFilePath);
        List<MetadataDto> metadataDtoList = metadataFromCsv.stream()
                .map(this::getMetadataDto).toList();
        metadataDtoList.forEach(metadataService::saveMetadata);
        log.info("Loaded {} rows into metadata", metadataService.getMetadataCount());
    }

    private void loadAndSaveSkudata(String csvFilePath) {
        log.info("Loading skudata from {}", csvFilePath);
        List<String[]> skuDataFromCsv = CsvReader.loadCSVData(csvFilePath);
        List<SkuDataDto> skuDataDtoList = skuDataFromCsv.stream()
                .map(this::getSkuDataDto).filter(Objects::nonNull).toList();
        skuDataDtoList.forEach(dto -> skuDataService.saveSkudata(dto, saveNewMetadataFromSkuData));
        log.info("Loaded {} rows into skudata", skuDataService.getSkudataCount());
        log.info("Final metadata count {}", metadataService.getMetadataCount());
    }

    private MetadataDto getMetadataDto(String[] csvData) {
        String locationFromCsv = csvData[0].trim();
        String departmentFromCsv = csvData[1].trim();
        String categoryFromCsv = csvData[2].trim();
        String subCategoryFromCsv = csvData[3].trim();
        return new MetadataDto(locationFromCsv, departmentFromCsv,
                categoryFromCsv, subCategoryFromCsv);
    }

    private SkuDataDto getSkuDataDto(String[] csvData) {
        String skuStr = csvData[0].trim();
        Long sku = null;
        try {
            sku = Long.parseLong(skuStr);
        } catch (NumberFormatException e) {
            log.error("Invalid sku id {} provided. Skip save skuData", skuStr);
            return null;
        }
        String name = csvData[1].trim();
        String locationFromCsv = csvData[2].trim();
        String departmentFromCsv = csvData[3].trim();
        String categoryFromCsv = csvData[4].trim();
        String subCategoryFromCsv = csvData[5].trim();
        return new SkuDataDto(sku, name, locationFromCsv, departmentFromCsv,
                categoryFromCsv, subCategoryFromCsv);
    }

}
