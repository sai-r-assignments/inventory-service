package com.example.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuDataDto extends MetadataDto {

    @Min(1)
    private Long sku;

    @NotBlank(message = "Invalid name value")
    private String name;

    public SkuDataDto(Long sku, String name, String locationId,
                      String departmentId, String categoryId, String subCategoryId) {
        super(locationId, departmentId, categoryId, subCategoryId);
        this.sku = sku;
        this.name = name;
    }

    @Override
    public String toString() {
        return "SkuDataDto{" +
                "sku=" + sku +
                ", name='" + name + '\'' +
                '}';
    }
}
