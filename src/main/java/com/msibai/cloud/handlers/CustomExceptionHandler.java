package com.msibai.cloud.handlers;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {

    if (ex.getBindingResult().getFieldError() != null) {
      String errorMessage =
          "Validation failed: " + ex.getBindingResult().getFieldError().getDefaultMessage();
      return ResponseEntity.badRequest().body(errorMessage);
    }

    StringBuilder errorMessage = new StringBuilder("Validation failed: ");
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errorMessage.append(error.getDefaultMessage()).append("; ");
    }

    return ResponseEntity.badRequest().body(errorMessage.toString());
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<String> handleDuplicateKeyException(DuplicateKeyException ex) {
    String errorMessage = "Duplicate key violation: " + ex.getMessage();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
}
