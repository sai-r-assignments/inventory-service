package com.example.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDto {

    @NotBlank(message = "Invalid locationId value")
    private String locationId;

    @NotBlank(message = "Invalid departmentId value")
    private String departmentId;

    @NotBlank(message = "Invalid categoryId value")
    private String categoryId;

    @NotBlank(message = "Invalid subCategoryId value")
    private String subCategoryId;

    @Override
    public String toString() {
        return "MetadataDto{" +
                "locationId='" + locationId + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", subCategoryId='" + subCategoryId + '\'' +
                '}';
    }
}
