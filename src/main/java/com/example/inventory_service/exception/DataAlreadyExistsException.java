package com.example.inventory_service.exception;

public class DataAlreadyExistsException extends RuntimeException {

    public DataAlreadyExistsException(String message) {
        super(message);
    }
}

