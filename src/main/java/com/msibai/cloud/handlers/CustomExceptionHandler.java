package com.msibai.cloud.handlers;

import com.msibai.cloud.exceptions.FolderCreationException;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.RootFolderAlreadyExistsException;
import com.msibai.cloud.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
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

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
    String errorMessage = "Access Denied: Invalid username or password. " + ex.getMessage();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<String> handleSignatureException(SignatureException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Access Denied: Token signature is invalid - " + ex.getMessage());
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Access Denied: Token has expired - " + ex.getMessage());
  }

  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<String> handleMalformedJwtException(MalformedJwtException ex) {
    if (ex.getCause() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied: Malformed JWT");
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Access Denied: " + ex.getCause().getMessage());
    }
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(FolderCreationException.class)
  public ResponseEntity<String> handleFolderCreationException(FolderCreationException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  @ExceptionHandler(RootFolderAlreadyExistsException.class)
  public ResponseEntity<String> handleRootFolderAlreadyExistsException(
      RootFolderAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}
