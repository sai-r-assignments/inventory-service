package com.example.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank(message = "Invalid email value") String email,
                              @NotBlank(message = "Invalid password value") String password) {

}
