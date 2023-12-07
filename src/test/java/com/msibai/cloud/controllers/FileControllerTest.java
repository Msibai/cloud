package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FileServiceImpl;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

  private final String token = "validToken";
  @Mock private FileServiceImpl fileServiceImpl;
  @InjectMocks private FileController fileController;

  @Test
  void testUploadFileToFolderSuccessfully() throws IOException, NoSuchAlgorithmException {
    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    UUID folderId = UUID.randomUUID();
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

    ResponseEntity<String> response = fileController.uploadFile(mockUser, folderId, mockFile);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("test.txt uploaded successfully!", response.getBody());
    verify(fileServiceImpl, times(1)).uploadFileToFolder(eq(mockUser), eq(folderId), any());
  }

  @Test
  void testUploadFileToFolderNullFile() throws IOException, NoSuchAlgorithmException {
    User mockUser = new User();
    UUID folderId = UUID.randomUUID();

    fileController.uploadFile(mockUser, folderId, null);

    verify(fileServiceImpl, never()).uploadFileToFolder(any(), any(), any());
  }

  @Test
  public void testUploadFileSizeExceedLimit() throws IOException, NoSuchAlgorithmException {
    User mockUser = new User();
    UUID folderId = UUID.randomUUID();
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "largeFile.txt", "text/plain", new byte[2097153000]);

    fileController.uploadFile(mockUser, folderId, mockFile);

    verify(fileServiceImpl, never()).uploadFileToFolder(any(), any(), any());
  }

  @Test
  void testDownloadFileFromFolderSuccessfully() {

    byte[] fileContent = "Test file content".getBytes();
    FileDto fileDto = new FileDto();
    fileDto.setContent(fileContent);
    fileDto.setName("test.txt");
    fileDto.setContentType("text/plain");

    when(fileServiceImpl.downloadFileFromFolder(anyString(), any(UUID.class), any(UUID.class)))
        .thenReturn(fileDto);

    ResponseEntity<byte[]> response =
        fileController.downloadFileFromFolder(token, UUID.randomUUID(), UUID.randomUUID());

    verify(fileServiceImpl, times(1))
        .downloadFileFromFolder(eq(token), any(UUID.class), any(UUID.class));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(fileContent, response.getBody());
    assertNotNull(response.getHeaders().getContentType());
    assertEquals(fileDto.getName(), response.getHeaders().getContentDisposition().getFilename());
  }

  @Test
  void testDownloadFileFromFolderNotFound() {

    when(fileServiceImpl.downloadFileFromFolder(anyString(), any(UUID.class), any(UUID.class)))
        .thenThrow(new NotFoundException("File not found"));

    try {
      ResponseEntity<byte[]> response =
          fileController.downloadFileFromFolder(token, UUID.randomUUID(), UUID.randomUUID());
      fail("fail");
    } catch (NotFoundException ex) {
      assertEquals("File not found", ex.getMessage());
    }
  }

  @Test
  void testDownloadFileFromFolderNotAuthorized() {

    when(fileServiceImpl.downloadFileFromFolder(anyString(), any(UUID.class), any(UUID.class)))
        .thenThrow(new UnauthorizedException("Unauthorized access"));

    try {
      ResponseEntity<byte[]> response =
          fileController.downloadFileFromFolder(token, UUID.randomUUID(), UUID.randomUUID());
      fail("fail");
    } catch (UnauthorizedException ex) {
      assertEquals("Unauthorized access", ex.getMessage());
    }
  }

  @Test
  void testDeleteFileFromFolder() {
    UUID folderId = UUID.randomUUID();
    UUID fileId = UUID.randomUUID();
    doNothing().when(fileServiceImpl).deleteFileFromFolder(token, folderId, fileId);

    ResponseEntity<String> response = fileController.deleteFileFromFolder(token, folderId, fileId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("File deleted successfully", response.getBody());
    verify(fileServiceImpl, times(1)).deleteFileFromFolder(token, folderId, fileId);
  }

  @Test
  void testMoveFileToAnotherFolder() {

    UUID currentFolderId = UUID.randomUUID();
    UUID fileId = UUID.randomUUID();
    UUID targetFolderId = UUID.randomUUID();
    doNothing()
        .when(fileServiceImpl)
        .moveFileToAnotherFolder(token, currentFolderId, fileId, targetFolderId);

    ResponseEntity<String> response =
        fileController.moveFileToAnotherFolder(token, currentFolderId, fileId, targetFolderId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("File moved successfully to the target folder.", response.getBody());

    verify(fileServiceImpl, times(1))
        .moveFileToAnotherFolder(token, currentFolderId, fileId, targetFolderId);
  }
}
