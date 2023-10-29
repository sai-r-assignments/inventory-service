package com.example.inventory_service.repository;

import com.example.inventory_service.domain.InventoryDetails;
import com.example.inventory_service.domain.Location;
import com.example.inventory_service.domain.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {
    Optional<Metadata> findByLocationAndInventoryDetails(Location location, InventoryDetails inventoryDetails);

    List<Metadata> findByLocation(Location location);

    List<Metadata> findByLocationAndInventoryDetails_Department(Location location, String department);

    List<Metadata> findByLocationAndInventoryDetails_DepartmentAndInventoryDetails_Category(
            Location location, String department, String category);
}