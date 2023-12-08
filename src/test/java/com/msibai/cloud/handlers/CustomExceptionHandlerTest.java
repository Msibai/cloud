package com.msibai.cloud.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.msibai.cloud.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.nio.file.FileAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

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

    ResponseEntity<String> responseEntity =
        customExceptionHandler.handleDuplicateKeyException(exception);

    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    assertEquals(
        "Duplicate key violation: The provided key already exists in the database",
        responseEntity.getBody());
  }

  @Test
  void testHandleBadCredentialsException() {
    BadCredentialsException exception = mock(BadCredentialsException.class);
    when(exception.getMessage()).thenReturn("Bad credentials");
    ResponseEntity<String> response =
        customExceptionHandler.handleBadCredentialsException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(
        "Access Denied: Invalid username or password. Bad credentials", response.getBody());
  }

  @Test
  void testHandleSignatureException() {
    SignatureException exception = mock(SignatureException.class);
    when(exception.getMessage()).thenReturn("Signature is invalid");
    ResponseEntity<String> response = customExceptionHandler.handleSignatureException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(
        "Access Denied: Token signature is invalid - Signature is invalid", response.getBody());
  }

  @Test
  void testHandleExpiredJwtException() {
    ExpiredJwtException exception = mock(ExpiredJwtException.class);
    when(exception.getMessage()).thenReturn("Token has expired");

    ResponseEntity<String> response = customExceptionHandler.handleExpiredJwtException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Access Denied: Token has expired - Token has expired", response.getBody());
  }

  @Test
  void testHandleMalformedJwtException() {
    MalformedJwtException mockException = mock(MalformedJwtException.class);
    mockException.initCause(null);

    ResponseEntity<String> response =
        customExceptionHandler.handleMalformedJwtException(mockException);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Access Denied: Malformed JWT", response.getBody());
  }

  @Test
  void testHandleNotFoundException() {
    NotFoundException exception = mock(NotFoundException.class);
    when(exception.getMessage()).thenReturn("Not Found");

    ResponseEntity<String> response = customExceptionHandler.handleNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not Found", response.getBody());
  }

  @Test
  void testHandleUnauthorizedException() {
    UnauthorizedException exception = mock(UnauthorizedException.class);
    when(exception.getMessage()).thenReturn("Unauthorized access");

    ResponseEntity<String> response = customExceptionHandler.handleUnauthorizedException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Unauthorized access", response.getBody());
  }

  @Test
  void testHandleFolderCreationException() {
    FolderCreationException exception = mock(FolderCreationException.class);
    when(exception.getMessage()).thenReturn("Failed to create root folder: ");

    ResponseEntity<String> response =
        customExceptionHandler.handleFolderCreationException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create root folder: ", response.getBody());
  }

  @Test
  void testHandleRootFolderAlreadyExistsException() {
    RootFolderAlreadyExistsException exception = mock(RootFolderAlreadyExistsException.class);
    when(exception.getMessage()).thenReturn("User already has a root directory.");

    ResponseEntity<String> response =
        customExceptionHandler.handleRootFolderAlreadyExistsException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("User already has a root directory.", response.getBody());
  }

  @Test
  void testHandleFolderNameNotUniqueException() {
    FolderNameNotUniqueException exception = mock(FolderNameNotUniqueException.class);
    when(exception.getMessage()).thenReturn("Folder name must be unique within the directory.");

    ResponseEntity<String> response =
        customExceptionHandler.handleFolderNameNotUniqueException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Folder name must be unique within the directory.", response.getBody());
  }

  @Test
  void testHandleFolderUpdateException() {
    FolderUpdateException exception = mock(FolderUpdateException.class);
    when(exception.getMessage()).thenReturn("Failed to rename folder");

    ResponseEntity<String> response = customExceptionHandler.handleFolderUpdateException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Failed to rename folder", response.getBody());
  }

  @Test
  void testHandleIncompleteFileDetailsException() {
    IncompleteFileDetailsException exception = mock(IncompleteFileDetailsException.class);
    when(exception.getMessage()).thenReturn("File details are incomplete.");

    ResponseEntity<String> response =
        customExceptionHandler.handleIncompleteFileDetailsException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("File details are incomplete.", response.getBody());
  }

  @Test
  void testHandleFileAlreadyExistsException() {
    FileAlreadyExistsException exception = mock(FileAlreadyExistsException.class);
    when(exception.getMessage())
        .thenReturn("File with the same name and type already exists in the folder.");

    ResponseEntity<String> response =
        customExceptionHandler.handleFileAlreadyExistsException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals(
        "File with the same name and type already exists in the folder.", response.getBody());
  }

  @Test
  void testHandleEncryptionException() {
    EncryptionException exception = mock(EncryptionException.class);
    when(exception.getMessage()).thenReturn("Encryption failed!");

    ResponseEntity<String> response = customExceptionHandler.handleEncryptionException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Encryption failed!", response.getBody());
  }

  @Test
  void testHandleFileUploadException() {
    FileUploadException exception = mock(FileUploadException.class);
    when(exception.getMessage()).thenReturn("Failed to upload file");

    ResponseEntity<String> response = customExceptionHandler.handleFileUploadException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to upload file", response.getBody());
  }

  @Test
  void testHandleMaxUploadSizeExceededException() {
    MaxUploadSizeExceededException exception = mock(MaxUploadSizeExceededException.class);
    when(exception.getMessage()).thenReturn("Maximum upload size exceeded.");

    ResponseEntity<String> response =
        customExceptionHandler.handleMaxUploadSizeExceededException(exception);

    assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
    assertEquals("Maximum upload size exceeded.", response.getBody());
  }

  @Test
  void testHandleMultipartException() {
    MultipartException exception = mock(MultipartException.class);
    when(exception.getMessage()).thenReturn("Multipart request handling error");

    ResponseEntity<String> response = customExceptionHandler.handleMultipartException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Multipart request handling error", response.getBody());
  }

  @Test
  void testHandleInvalidPaginationParameterException() {
    InvalidPaginationParameterException exception = mock(InvalidPaginationParameterException.class);
    when(exception.getMessage()).thenReturn("Invalid pagination parameters.");

    ResponseEntity<String> response =
        customExceptionHandler.handleInvalidPaginationParameterException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid pagination parameters.", response.getBody());
  }
}
