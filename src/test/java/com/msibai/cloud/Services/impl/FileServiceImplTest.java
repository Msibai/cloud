package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.Utility.getFolderByIdOrThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.mappers.impl.FileMapperImpl;
import com.msibai.cloud.repositories.FileRepository;
import com.msibai.cloud.repositories.FolderRepository;
import com.msibai.cloud.utilities.Utility;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

  @Mock FileRepository fileRepository;
  @Mock FileMapperImpl fileMapperImpl;
  @InjectMocks FileServiceImpl fileServiceImpl;

  @Test
  void testUploadFileSuccessfully() throws FileAlreadyExistsException, NoSuchAlgorithmException {
    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    UUID folderId = UUID.randomUUID();
    byte[] fileContent = "File content".getBytes();
    FileDto mockFileDto =
        FileDto.builder().name("test.txt").contentType("text/plain").content(fileContent).build();

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(folderId), any(FolderRepository.class)))
          .thenReturn(new Folder());

      when(fileRepository.findByNameAndContentTypeAndFolderId(
              anyString(), anyString(), any(UUID.class)))
          .thenReturn(Optional.empty());
      when(fileMapperImpl.mapFrom(any(FileDto.class))).thenReturn(new File());

      fileServiceImpl.uploadFileToFolder(mockUser, folderId, mockFileDto);

      verify(fileRepository, times(1)).save(any());
    }
  }

  @Test
  void testUploadFileAlreadyExists() {
    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    UUID folderId = UUID.randomUUID();
    byte[] fileContent = "File content".getBytes();
    FileDto mockFileDto =
        FileDto.builder()
            .name("existingFile.txt")
            .contentType("text/plain")
            .content(fileContent)
            .build();

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(folderId), any(FolderRepository.class)))
          .thenReturn(new Folder());

      when(fileRepository.findByNameAndContentTypeAndFolderId(
              anyString(), anyString(), any(UUID.class)))
          .thenReturn(Optional.of(new File()));

      assertThrows(
          FileAlreadyExistsException.class,
          () -> fileServiceImpl.uploadFileToFolder(mockUser, folderId, mockFileDto));
      verify(fileRepository, never()).save(any());
    }
  }
}
