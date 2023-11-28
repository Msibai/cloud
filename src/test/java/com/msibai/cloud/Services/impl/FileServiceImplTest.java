package com.msibai.cloud.Services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.repositories.FileRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

  @Mock JwtService jwtService;
  @Mock FolderServiceImpl folderServiceImpl;
  @Mock FileRepository fileRepository;

  @InjectMocks FileServiceImpl fileServiceImpl;

  @Test
  void uploadFileToFolderSuccessfully() {

    String token = "validToken";
    UUID userId = UUID.randomUUID();
    UUID folderId = UUID.randomUUID();

    FileDto file =
        FileDto.builder()
            .name("Test.txt")
            .contentType("text/plain")
            .content(new byte[] {})
            .size(100L)
            .build();

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));
    when(folderServiceImpl.findFolderByIdAndUserId(folderId, token))
        .thenReturn(Optional.of(new Folder()));

    fileServiceImpl.uploadFileToFolder(token, folderId, file);

    verify(jwtService).extractUserId(token);
    verify(folderServiceImpl).findFolderByIdAndUserId(eq(folderId), anyString());
    verify(fileRepository).save(any());
  }

  @Test
  void uploadFileToFolderFailure() {

    String token = "validToken";
    UUID userId = UUID.randomUUID();
    UUID folderId = UUID.randomUUID();

    FileDto file =
        FileDto.builder()
            .name("Test.txt")
            .contentType("text/plain")
            .content(new byte[] {})
            .size(100L)
            .build();

    when(fileRepository.findByName(file.getName())).thenReturn(Optional.of(new File()));

    assertThrows(
        DuplicateKeyException.class,
        () -> fileServiceImpl.uploadFileToFolder(token, folderId, file));
    verify(jwtService, never()).extractUserId(token);
    verify(folderServiceImpl, never()).findFolderByIdAndUserId(eq(folderId), anyString());
    verify(fileRepository, never()).save(any());
  }

  @Test
  void downloadFileFromFolder() {}

  @Test
  void deleteFileFromFolder() {}

  @Test
  void moveFileToAnotherFolder() {}
}
