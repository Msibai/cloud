package com.msibai.cloud.Services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
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

  private final UUID fileId = UUID.randomUUID();
  private final UUID folderId = UUID.randomUUID();
  private final UUID userId = UUID.randomUUID();
  private final String token = "validToken";
  private final File file =
      File.builder()
          .id(fileId)
          .name("testFile.txt")
          .folderId(folderId)
          .userId(userId)
          .contentType("text/plain")
          .content(new byte[] {})
          .size(100L)
          .build();

  @Mock JwtService jwtService;
  @Mock FolderServiceImpl folderServiceImpl;
  @Mock FileRepository fileRepository;
  @InjectMocks FileServiceImpl fileServiceImpl;

  @Test
  void uploadFileToFolderSuccessfully() {

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
  void downloadFileFromFolderSuccessfully() {

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));

    FileDto expectedFile =
        FileDto.builder()
            .name(file.getName())
            .content(file.getContent())
            .contentType(file.getContentType())
            .size(file.getSize())
            .build();

    when(fileRepository.findByIdAndFolderId(fileId, folderId)).thenReturn(Optional.of(file));

    FileDto downloadedFileDto = fileServiceImpl.downloadFileFromFolder(token, folderId, fileId);

    assertEquals(expectedFile.getName(), downloadedFileDto.getName());
  }

  @Test
  void downloadFileFromFolderFileNotFound() {

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));
    when(fileRepository.findByIdAndFolderId(fileId, folderId)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> fileServiceImpl.downloadFileFromFolder(token, folderId, fileId));
  }

  @Test
  void downloadFileFromFolderUnauthorized() {

    file.setUserId(UUID.randomUUID());

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));
    when(fileRepository.findByIdAndFolderId(fileId, folderId)).thenReturn(Optional.of(file));

    assertThrows(
        UnauthorizedException.class,
        () -> fileServiceImpl.downloadFileFromFolder(token, folderId, fileId));
  }

  @Test
  void deleteFileFromFolderSuccessfully() {

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));
    when(fileRepository.findByIdAndFolderId(fileId, folderId)).thenReturn(Optional.of(file));

    assertDoesNotThrow(() -> fileServiceImpl.deleteFileFromFolder(token, folderId, fileId));
    verify(fileRepository, times(1)).delete(file);
  }

  @Test
  void deleteFileFromFolderUnauthorized() {

    file.setUserId(UUID.randomUUID());

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));
    when(fileRepository.findByIdAndFolderId(fileId, folderId)).thenReturn(Optional.of(file));

    assertThrows(
        UnauthorizedException.class,
        () -> fileServiceImpl.deleteFileFromFolder(token, folderId, fileId));
    verify(fileRepository, never()).delete(file);
  }

  @Test
  void deleteFileFromFolderFileNotFound() {

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));
    when(fileRepository.findByIdAndFolderId(fileId, folderId)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> fileServiceImpl.deleteFileFromFolder(token, folderId, fileId));
    verify(fileRepository, never()).delete(file);
  }

  @Test
  void moveFileToAnotherFolder() {

    UUID currentFolderId = UUID.randomUUID();
    UUID fileId = UUID.randomUUID();
    UUID targetFolderId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(jwtService.extractUserId(token)).thenReturn(String.valueOf(userId));

    File fileToMove = new File();
    fileToMove.setUserId(userId);
    when(fileRepository.findByIdAndFolderId(fileId, currentFolderId))
        .thenReturn(Optional.of(fileToMove));

    assertDoesNotThrow(
        () ->
            fileServiceImpl.moveFileToAnotherFolder(
                token, currentFolderId, fileId, targetFolderId));
    verify(fileRepository, times(1)).save(fileToMove);
  }
}
