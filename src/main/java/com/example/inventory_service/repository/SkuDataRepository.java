package com.example.inventory_service.repository;

import com.example.inventory_service.domain.Metadata;
import com.example.inventory_service.domain.SkuData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkuDataRepository extends JpaRepository<SkuData, Long> {
    List<SkuData> findByMetadata(Metadata metadata);
}