package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.Utility.getFolderByIdOrThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
import com.msibai.cloud.mappers.impl.FileMapperImpl;
import com.msibai.cloud.repositories.FileRepository;
import com.msibai.cloud.repositories.FolderRepository;
import com.msibai.cloud.utilities.FileEncryption;
import com.msibai.cloud.utilities.Utility;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.junit.jupiter.api.BeforeEach;
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

  private User mockUser;
  private UUID userId;
  private UUID folderId;
  private UUID fileId;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    userId = UUID.randomUUID();
    mockUser.setId(userId);
    folderId = UUID.randomUUID();
    fileId = UUID.randomUUID();
  }

  @Test
  void testUploadFileSuccessfully() throws FileAlreadyExistsException, NoSuchAlgorithmException {
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

  @Test
  public void testDownloadFileSuccessfully() throws NoSuchAlgorithmException {

    SecretKey key = FileEncryption.generateKey(128);
    IvParameterSpec iv = FileEncryption.generateIv();
    byte[] originalContent = "This is a test file content.".getBytes();
    byte[] encryptedContent =
        FileEncryption.encryptFile("AES/CBC/PKCS5Padding", key, iv, originalContent);

    File file = new File();
    file.setId(fileId);
    file.setUserId(userId);
    file.setContent(encryptedContent);
    file.setEncryptionKey(Base64.getEncoder().encodeToString(key.getEncoded()));
    file.setIv(Base64.getEncoder().encodeToString(iv.getIV()));

    FileDto mockedFileDto = new FileDto();

    when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
    when(fileMapperImpl.mapTo(file)).thenReturn(mockedFileDto); // Create a

    FileDto result = fileServiceImpl.downloadFile(mockUser, fileId);

    verify(fileRepository, times(1)).findById(fileId);
    verify(fileMapperImpl, times(1)).mapTo(file);
    assertNotNull(result);
    assertEquals(mockedFileDto.getContent(), result.getContent());
  }

  @Test
  public void testDownloadFileUnauthorizedUser() {

    File anotherUserFile = new File();
    anotherUserFile.setId(fileId);
    anotherUserFile.setUserId(UUID.randomUUID());

    when(fileRepository.findById(fileId)).thenReturn(Optional.of(anotherUserFile));

    assertThrows(UnauthorizedException.class, () -> fileServiceImpl.downloadFile(mockUser, fileId));
    verify(fileRepository, times(1)).findById(fileId);
    verifyNoInteractions(fileMapperImpl);
  }

  @Test
  public void testDownloadFileFileNotFound() {

    when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> fileServiceImpl.downloadFile(mockUser, fileId));
    verify(fileRepository, times(1)).findById(fileId);
    verifyNoInteractions(fileMapperImpl);
  }

  @Test
  void testDeleteFileSuccessfully() {

    File file = new File();
    file.setId(fileId);
    file.setUserId(userId);

    when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

    fileServiceImpl.deleteFile(mockUser, fileId);

    verify(fileRepository, times(1)).findById(fileId);
    verify(fileRepository, times(1)).delete(file);
  }

  @Test
  void testDeleteFileNotFound() {
    when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> fileServiceImpl.deleteFile(mockUser, fileId));

    verify(fileRepository, times(1)).findById(fileId);
    verifyNoMoreInteractions(fileRepository);
  }

  @Test
  void testDeleteFileUnauthorizedUser() {

    File anotherUserFile = new File();
    anotherUserFile.setId(fileId);
    anotherUserFile.setUserId(UUID.randomUUID());

    when(fileRepository.findById(fileId)).thenReturn(Optional.of(anotherUserFile));

    assertThrows(UnauthorizedException.class, () -> fileServiceImpl.deleteFile(mockUser, fileId));

    verify(fileRepository, times(1)).findById(fileId);
    verifyNoMoreInteractions(fileRepository);
  }
}
