package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FileServiceImpl;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.User;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

  @Mock private FileServiceImpl fileServiceImpl;
  @InjectMocks private FileController fileController;

  private User mockUser;
  private UUID folderId;
  private UUID fileId;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    UUID userId = UUID.randomUUID();
    mockUser.setId(userId);
    folderId = UUID.randomUUID();
    fileId = UUID.randomUUID();
  }

  @Test
  void testUploadFileToFolderSuccessfully() throws IOException, NoSuchAlgorithmException {
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

    ResponseEntity<String> response = fileController.uploadFile(mockUser, folderId, mockFile);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("test.txt uploaded successfully!", response.getBody());
    verify(fileServiceImpl, times(1)).uploadFileToFolder(eq(mockUser), eq(folderId), any());
  }

  @Test
  void testUploadFileToFolderNullFile() throws IOException, NoSuchAlgorithmException {

    fileController.uploadFile(mockUser, folderId, null);

    verify(fileServiceImpl, never()).uploadFileToFolder(any(), any(), any());
  }

  @Test
  public void testUploadFileSizeExceedLimit() throws IOException, NoSuchAlgorithmException {

    MockMultipartFile mockFile =
        new MockMultipartFile("file", "largeFile.txt", "text/plain", new byte[2097153000]);

    fileController.uploadFile(mockUser, folderId, mockFile);

    verify(fileServiceImpl, never()).uploadFileToFolder(any(), any(), any());
  }

  @Test
  public void testDownloadFileSuccessfully() {
    byte[] fileContent = "Your file content".getBytes();
    String fileName = "test-file.txt";
    String contentType = "text/plain";

    FileDto mockFile = new FileDto();
    mockFile.setContent(fileContent);
    mockFile.setName(fileName);
    mockFile.setContentType(contentType);
    when(fileServiceImpl.downloadFile(any(User.class), eq(fileId))).thenReturn(mockFile);

    ResponseEntity<byte[]> response = fileController.downloadFile(new User(), fileId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(fileContent.length, Objects.requireNonNull(response.getBody()).length);
  }

  @Test
  public void testDownloadFileFailureFileNotFound() {

    when(fileServiceImpl.downloadFile(any(User.class), eq(fileId))).thenReturn(null);

    ResponseEntity<byte[]> response = fileController.downloadFile(new User(), fileId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testDeleteFile() {
    doNothing().when(fileServiceImpl).deleteFile(mockUser, fileId);

    ResponseEntity<String> response = fileController.deleteFile(mockUser, fileId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("File deleted successfully", response.getBody());
    verify(fileServiceImpl, times(1)).deleteFile(mockUser, fileId);
  }

  @Test
  void testMoveFileToAnotherFolder() {

    UUID currentFolderId = UUID.randomUUID();
    UUID targetFolderId = UUID.randomUUID();
    doNothing()
        .when(fileServiceImpl)
        .moveFileToAnotherFolder(mockUser, currentFolderId, fileId, targetFolderId);

    ResponseEntity<String> response =
        fileController.moveFileToAnotherFolder(mockUser, currentFolderId, fileId, targetFolderId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("File moved successfully to the target folder.", response.getBody());

    verify(fileServiceImpl, times(1))
        .moveFileToAnotherFolder(mockUser, currentFolderId, fileId, targetFolderId);
  }
}
