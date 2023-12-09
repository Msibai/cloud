package com.msibai.cloud.handlers;

import com.msibai.cloud.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.nio.file.FileAlreadyExistsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

/**
 * Global exception handler that manages and provides custom responses for various exceptions
 * occurring during the application's runtime.
 */
@RestControllerAdvice
public class CustomExceptionHandler {

  /**
   * Handles validation exceptions from method argument validation errors.
   *
   * @param ex The MethodArgumentNotValidException instance.
   * @return ResponseEntity containing a response for validation errors.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {

    // The exception is thrown when method argument validation fails.
    if (ex.getBindingResult().getFieldError() != null) {
      // If there's a specific field error, returns a bad request with that error message.
      String errorMessage =
          "Validation failed: " + ex.getBindingResult().getFieldError().getDefaultMessage();
      return ResponseEntity.badRequest().body(errorMessage);
    }

    // Constructs an error message for global errors if no specific field error is found.
    StringBuilder errorMessage = new StringBuilder("Validation failed: ");
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errorMessage.append(error.getDefaultMessage()).append("; ");
    }

    // Returns a bad request response indicating failed validation with global errors.
    return ResponseEntity.badRequest().body(errorMessage.toString());
  }

  /**
   * Manages exceptions arising from database duplicate key violations.
   *
   * @param ex The DuplicateKeyException instance.
   * @return ResponseEntity with a response for duplicate key violations.
   */
  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<String> handleDuplicateKeyException(DuplicateKeyException ex) {

    // This exception is thrown when attempting to insert or update data violating a primary key or
    // unique constraint.
    String errorMessage = "Duplicate key violation: " + ex.getMessage();

    // Returns a response indicating a conflict due to duplicate key violation.
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }

  /**
   * Handles exceptions thrown when authentication credentials are invalid.
   *
   * @param ex The BadCredentialsException instance.
   * @return ResponseEntity with a response for invalid authentication credentials.
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {

    // The exception is thrown when authentication credentials (username/password) are incorrect or
    // missing.
    String errorMessage = "Access Denied: Invalid username or password. " + ex.getMessage();

    // Returns a response indicating the failure of authentication due to invalid credentials.
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
  }

  /**
   * Handles exceptions caused by invalid JWT signature during token verification.
   *
   * @param ex The SignatureException instance.
   * @return ResponseEntity with a response for invalid token signature.
   */
  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<String> handleSignatureException(SignatureException ex) {

    // This exception is thrown when a token's signature validation fails.
    String errorMessage = "Access Denied: Token signature is invalid - " + ex.getMessage();

    // Returns a response indicating unauthorized access due to an invalid token signature.
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
  }

  /**
   * Handles exceptions thrown when a JWT token has expired.
   *
   * @param ex The ExpiredJwtException instance.
   * @return ResponseEntity with a response indicating token expiration.
   */
  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {

    // The exception is thrown when a JWT token is presented, but it has expired.
    String errorMessage = "Access Denied: Token has expired - " + ex.getMessage();

    // Returns a response indicating that the token has expired.
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
  }

  /**
   * Handles exceptions thrown when a JWT token is malformed.
   *
   * @param ex The MalformedJwtException instance.
   * @return ResponseEntity with a response indicating a malformed JWT.
   */
  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<String> handleMalformedJwtException(MalformedJwtException ex) {

    // The exception is thrown when a JWT token is presented, but it's malformed.
    if (ex.getCause() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied: Malformed JWT");
    } else {
      // Returns a response indicating a malformed JWT.
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Access Denied: " + ex.getCause().getMessage());
    }
  }

  /**
   * Handles exceptions related to resource not found.
   *
   * @param ex The NotFoundException instance.
   * @return ResponseEntity indicating the resource was not found.
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {

    // Returns a response indicating the resource was not found.
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Handles exceptions related to unauthorized access.
   *
   * @param ex The UnauthorizedException instance.
   * @return ResponseEntity indicating unauthorized access.
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {

    // Returns a response indicating unauthorized access.
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  /**
   * Handles exceptions related to folder creation failure.
   *
   * @param ex The FolderCreationException instance.
   * @return ResponseEntity indicating folder creation failure.
   */
  @ExceptionHandler(FolderCreationException.class)
  public ResponseEntity<String> handleFolderCreationException(FolderCreationException ex) {

    // Returns a response indicating folder creation failure.
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  /**
   * Handles exceptions when a root folder already exists.
   *
   * @param ex The RootFolderAlreadyExistsException instance.
   * @return ResponseEntity indicating the conflict due to an existing root folder.
   */
  @ExceptionHandler(RootFolderAlreadyExistsException.class)
  public ResponseEntity<String> handleRootFolderAlreadyExistsException(
      RootFolderAlreadyExistsException ex) {

    // Returns a response indicating a conflict due to an existing root folder.
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles exceptions when a folder name is not unique.
   *
   * @param ex The FolderNameNotUniqueException instance.
   * @return ResponseEntity indicating the conflict due to a non-unique folder name.
   */
  @ExceptionHandler(FolderNameNotUniqueException.class)
  public ResponseEntity<String> handleFolderNameNotUniqueException(
      FolderNameNotUniqueException ex) {

    // Returns a response indicating a conflict due to a non-unique folder name.
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles exceptions when encountering issues with folder updates.
   *
   * @param ex The FolderUpdateException instance.
   * @return ResponseEntity indicating a conflict due to issues with updating the folder.
   */
  @ExceptionHandler(FolderUpdateException.class)
  public ResponseEntity<String> handleFolderUpdateException(FolderUpdateException ex) {

    // Returns a response indicating a conflict due to issues with updating the folder.
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles exceptions when encountering incomplete file details.
   *
   * @param ex The IncompleteFileDetailsException instance.
   * @return ResponseEntity indicating a bad request due to incomplete file details.
   */
  @ExceptionHandler(IncompleteFileDetailsException.class)
  public ResponseEntity<String> handleIncompleteFileDetailsException(
      IncompleteFileDetailsException ex) {

    // Returns a response indicating a bad request due to incomplete file details.
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Handles exceptions when encountering a file that already exists.
   *
   * @param ex The FileAlreadyExistsException instance.
   * @return ResponseEntity indicating a conflict due to a file already existing.
   */
  @ExceptionHandler(FileAlreadyExistsException.class)
  public ResponseEntity<String> handleFileAlreadyExistsException(FileAlreadyExistsException ex) {

    // Returns a response indicating a conflict due to a file already existing.
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles exceptions related to encryption errors.
   *
   * @param ex The EncryptionException instance.
   * @return ResponseEntity indicating an internal server error due to encryption failure.
   */
  @ExceptionHandler(EncryptionException.class)
  public ResponseEntity<String> handleEncryptionException(EncryptionException ex) {

    // Returns a response indicating an internal server error due to encryption failure.
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  /**
   * Handles exceptions related to file upload failures.
   *
   * @param ex The FileUploadException instance.
   * @return ResponseEntity indicating an internal server error due to file upload failure.
   */
  @ExceptionHandler(FileUploadException.class)
  public ResponseEntity<String> handleFileUploadException(FileUploadException ex) {

    // Returns a response indicating an internal server error due to file upload failure.
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  /**
   * Handles exceptions when the uploaded file size exceeds the maximum allowed size.
   *
   * @param ex The MaxUploadSizeExceededException instance.
   * @return ResponseEntity indicating a payload too large error due to exceeding the maximum file
   *     size.
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<String> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex) {

    // Returns a response indicating a payload too large error due to exceeding the maximum file
    // size.
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(ex.getMessage());
  }

  /**
   * Handles exceptions related to multipart requests.
   *
   * @param ex The MultipartException instance.
   * @return ResponseEntity indicating a bad request error due to issues with the multipart request.
   */
  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<String> handleMultipartException(MultipartException ex) {

    // Returns a response indicating a bad request error due to issues with the multipart request.
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Handles exceptions related to invalid pagination parameters.
   *
   * @param ex The InvalidPaginationParameterException instance.
   * @return ResponseEntity indicating a bad request error due to invalid pagination parameters.
   */
  @ExceptionHandler(InvalidPaginationParameterException.class)
  public ResponseEntity<String> handleInvalidPaginationParameterException(
      InvalidPaginationParameterException ex) {

    // Returns a response indicating a bad request error due to invalid pagination parameters.
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
}
