package com.example.inventory_service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorData {

    @NotEmpty
    private final String errorMessage;

    private Collection<String> additionalErrorData = new ArrayList<>();
}
