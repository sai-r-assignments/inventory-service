package com.example.inventory_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SkuData {

    @Id
    @Min(1)
    private Long sku;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "metadata_id", nullable = false)
    private Metadata metadata;

}
