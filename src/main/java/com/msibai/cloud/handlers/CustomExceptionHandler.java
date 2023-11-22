package com.msibai.cloud.handlers;

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
}
