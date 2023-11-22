package com.msibai.cloud.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {

  @InjectMocks CustomExceptionHandler customExceptionHandler;

  @Test
  void testHandleValidationExceptions() {
    FieldError fieldError = new FieldError("objectName", "fieldName", "Invalid value");
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldError()).thenReturn(fieldError);

    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);
    ResponseEntity<String> responseEntity =
        customExceptionHandler.handleValidationExceptions(exception);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Validation failed: Invalid value", responseEntity.getBody());
  }

  @Test
  void testHandleDuplicateKeyException() {
    DuplicateKeyException exception = mock(DuplicateKeyException.class);
    when(exception.getMessage()).thenReturn("The provided key already exists in the database");

    ResponseEntity<String> responseEntity = customExceptionHandler.handleDuplicateKeyException(exception);

    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    assertEquals("Duplicate key violation: The provided key already exists in the database", responseEntity.getBody());
  }
}
