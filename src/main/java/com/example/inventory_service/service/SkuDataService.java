package com.example.inventory_service.service;

import com.example.inventory_service.domain.Metadata;
import com.example.inventory_service.domain.SkuData;
import com.example.inventory_service.dto.MetadataDto;
import com.example.inventory_service.dto.SkuDataDto;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import com.example.inventory_service.repository.SkuDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkuDataService {

    private final SkuDataRepository skuDataRepository;

    private final MetadataService metadataService;

    public Collection<Long> getSkuDataWithMetadata(MetadataDto metadataDto) {
        Metadata metadata = null;
        try {
            metadata = metadataService.getMetadataFromInventoryDetails(metadataDto);
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException("Skudata not found with given details");
        }
        List<SkuData> skuData = skuDataRepository.findByMetadata(metadata);
        if (skuData.isEmpty()) {
            throw new DataNotFoundException("Skudata not found with given details");
        }
        return skuData.stream().map(SkuData::getSku).toList();
    }


    public long getSkudataCount() {
        return skuDataRepository.count();
    }

    @Transactional
    public void saveSkudata(SkuDataDto dto, boolean saveNewMetadata) {
        Metadata metadata = null;
        try {
            metadata = metadataService.getMetadataFromInventoryDetails(dto);
        } catch (DataNotFoundException mnfe) {
            if (!saveNewMetadata) throw mnfe;
            log.info("Saving metadata to db as for {} as it not exists in db", dto);
            metadata = metadataService.saveMetadata(dto);
        }
        saveSkuDataToDb(dto, metadata);
    }

    public void deleteSkudata(Long sku) {
        SkuData skuData = skuDataRepository.findById(sku)
                .orElseThrow(() -> new DataNotFoundException("No data found with given sku " + sku));
        skuDataRepository.delete(skuData);
    }

    private void saveSkuDataToDb(SkuDataDto dto, Metadata metadata) {
        SkuData skuData = new SkuData();
        skuData.setSku(dto.getSku());
        skuData.setName(dto.getName());
        skuData.setMetadata(metadata);
        skuDataRepository.save(skuData);
    }
}
