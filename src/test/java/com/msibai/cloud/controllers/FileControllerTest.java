package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FileServiceImpl;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
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
  @Mock private FileServiceImpl fileServiceImp;
  @InjectMocks private FileController fileController;

  @Test
  void testUploadFileToFolderSuccessfully() throws java.io.IOException {

    byte[] fileContent = "Test file content".getBytes();
    MockMultipartFile multipartFile =
        new MockMultipartFile("file", "test.txt", "text/plain", fileContent);

    doNothing().when(fileServiceImp).uploadFileToFolder(anyString(), any(UUID.class), any());

    ResponseEntity<String> response =
        fileController.uploadFileToFolder(token, UUID.randomUUID(), multipartFile);

    verify(fileServiceImp, times(1))
        .uploadFileToFolder(eq(token), any(UUID.class), any(FileDto.class));
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testUploadFileToFolderNullFile() throws java.io.IOException {

    ResponseEntity<String> response =
        fileController.uploadFileToFolder(token, UUID.randomUUID(), null);

    verifyNoInteractions(fileServiceImp);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testDownloadFileFromFolderSuccessfully() {

    byte[] fileContent = "Test file content".getBytes();
    FileDto fileDto = new FileDto();
    fileDto.setContent(fileContent);
    fileDto.setName("test.txt");
    fileDto.setContentType("text/plain");

    when(fileServiceImp.downloadFileFromFolder(anyString(), any(UUID.class), any(UUID.class)))
        .thenReturn(fileDto);

    ResponseEntity<byte[]> response =
        fileController.downloadFileFromFolder(token, UUID.randomUUID(), UUID.randomUUID());

    verify(fileServiceImp, times(1))
        .downloadFileFromFolder(eq(token), any(UUID.class), any(UUID.class));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(fileContent, response.getBody());
    assertNotNull(response.getHeaders().getContentType());
    assertEquals(fileDto.getName(), response.getHeaders().getContentDisposition().getFilename());
  }

  @Test
  void testDownloadFileFromFolderNotFound() {

    when(fileServiceImp.downloadFileFromFolder(anyString(), any(UUID.class), any(UUID.class)))
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

    when(fileServiceImp.downloadFileFromFolder(anyString(), any(UUID.class), any(UUID.class)))
        .thenThrow(new UnauthorizedException("Unauthorized access"));

    try {
      ResponseEntity<byte[]> response =
          fileController.downloadFileFromFolder(token, UUID.randomUUID(), UUID.randomUUID());
      fail("fail");
    } catch (UnauthorizedException ex) {
      assertEquals("Unauthorized access", ex.getMessage());
    }
  }
}
