package com.example.inventory_service.config;

import com.example.inventory_service.dto.ErrorData;
import com.example.inventory_service.exception.DataAlreadyExistsException;
import com.example.inventory_service.exception.DataNotFoundException;
import com.example.inventory_service.exception.InvalidOperationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collection;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorData> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Collection<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();
        ErrorData errorData = new ErrorData("Invalid request data", errors);
        return new ResponseEntity<>(errorData, BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorData> handleDataNotFoundException(
            DataNotFoundException ex, HttpServletRequest request) {
        ErrorData errorData = new ErrorData(ex.getMessage());
        return new ResponseEntity<>(errorData, NOT_FOUND);
    }

    @ExceptionHandler({DataAlreadyExistsException.class,
            InvalidOperationException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorData> handleBadOperations(
            RuntimeException ex, HttpServletRequest request) {
        ErrorData errorData = new ErrorData(ex.getMessage());
        return new ResponseEntity<>(errorData, BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorData> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        return new ResponseEntity<>(buildValidationErrors(ex.getConstraintViolations()),
                BAD_REQUEST);
    }

    private ErrorData buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        Collection<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage).toList();
        return new ErrorData("Invalid Data", violationMessages);
    }
}
