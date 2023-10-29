package com.example.inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LocationDto(@NotBlank(message = "Invalid locationId value") String locationId, String description) {

}
