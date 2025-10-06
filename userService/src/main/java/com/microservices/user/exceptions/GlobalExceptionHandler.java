package com.microservices.user.exceptions;

import com.microservices.user.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handlerResourceNotFoundException(ResourceNotFoundException ex) {
        String message = ex.getMessage();
        ApiResponse response = ApiResponse.builder()
                                            .message(message)
                                            .success(true)
                                            .status(HttpStatus.NOT_FOUND)
                                            .build();
        return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getMessage();
        message = ex.getBindingResult().getFieldError().getDefaultMessage();
        ApiResponse response = ApiResponse.builder()
                                            .message(message)
                                            .success(true)
                                            .status(HttpStatus.BAD_REQUEST)
                                            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse> handleDuplicateResourceException(
            DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage());

        ApiResponse errorResponse = ApiResponse.builder()
                .message(ex.getMessage())
                .success(false)
                .status(HttpStatus.CONFLICT)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

}
