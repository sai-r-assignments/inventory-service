package com.example.inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Invalid name value")
    private String name;

    @NotBlank(message = "Invalid email value")
    private String email;

    @Size(min = 6, message = "Password should contain minimum 6 characters")
    private String password;

    @JsonProperty
    private boolean isAdmin;
}
