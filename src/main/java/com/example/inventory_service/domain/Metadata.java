package com.example.inventory_service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(
        columnNames = {"location_loc_id", "department", "category", "subCategory"})})
@NoArgsConstructor
public class Metadata {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long metadataId;

    @ManyToOne
    @JoinColumn(name = "location_loc_id")
    private Location location;

    @Embedded
    private InventoryDetails inventoryDetails;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "metadata")
    private List<SkuData> skuDataList = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedTime;
}
