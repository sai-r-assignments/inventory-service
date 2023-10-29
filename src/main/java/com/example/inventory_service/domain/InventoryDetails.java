package com.example.inventory_service.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDetails {

    @NotEmpty
    private String department;

    @NotEmpty
    private String category;

    @NotEmpty
    private String subCategory;

}
