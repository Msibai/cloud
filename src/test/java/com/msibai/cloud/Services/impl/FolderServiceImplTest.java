package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.Utility.authorizeUserAccess;
import static com.msibai.cloud.utilities.Utility.getFolderByIdOrThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.*;
import com.msibai.cloud.mappers.impl.FolderMapperImpl;
import com.msibai.cloud.repositories.FolderRepository;
import com.msibai.cloud.utilities.Utility;
import java.util.*;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {
  @Mock FolderRepository folderRepository;
  @Mock FolderMapperImpl folderMapperImpl;
  @InjectMocks FolderServiceImpl folderServiceImpl;

  @Test
  void testCreateRootFolderForNewUserSuccessfully() {
    UUID userId = UUID.randomUUID();

    when(folderRepository.findByUserIdAndIsRootFolder(userId, true)).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> folderServiceImpl.createRootFolderForNewUser(userId));
    verify(folderRepository, times(1)).save(any());
  }

  @Test
  void testCreateRootFolderForNewUserRootFolderAlreadyExistsException() {
    UUID userId = UUID.randomUUID();

    when(folderRepository.findByUserIdAndIsRootFolder(userId, true))
        .thenReturn(Optional.of(new Folder()));

    assertThrows(
        RootFolderAlreadyExistsException.class,
        () -> folderServiceImpl.createRootFolderForNewUser(userId));
    verify(folderRepository, never()).save(any());
  }

  @Test
  public void testCreateFolderForUserSuccessfully() {

    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentFolderId = UUID.randomUUID();
    String folderName = "NewFolder";

    when(folderRepository.save(any(Folder.class))).thenReturn(new Folder());

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(parentFolderId), any(FolderRepository.class)))
          .thenReturn(new Folder());

      assertDoesNotThrow(
          () -> folderServiceImpl.createFolderForUser(user, parentFolderId, folderName));
      verify(folderRepository, Mockito.times(1)).save(any(Folder.class));
    }
  }

  @Test
  public void testCreateFolderForUserFailureFolderNotFound() {
    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentFolderId = UUID.randomUUID();
    String folderName = "NewFolder";

    try (MockedStatic<Utility> utilityClassMock = Mockito.mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(parentFolderId), any(FolderRepository.class)))
          .thenThrow(new NotFoundException("Folder not found with ID: " + parentFolderId));

      assertThrows(
          NotFoundException.class,
          () -> folderServiceImpl.createFolderForUser(user, parentFolderId, folderName));
    }
  }

  @Test
  public void testCreateFolderForUserFailureUnauthorizedAccess() {
    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentFolderId = UUID.randomUUID();
    String folderName = "NewFolder";

    try (MockedStatic<Utility> utilityClassMock = Mockito.mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(parentFolderId), any(FolderRepository.class)))
          .thenReturn(new Folder());

      utilityClassMock
          .when(() -> authorizeUserAccess(any(Folder.class), eq(user.getId()), any(Function.class)))
          .thenThrow(new UnauthorizedException("Unauthorized access!"));

      assertThrows(
          UnauthorizedException.class,
          () -> folderServiceImpl.createFolderForUser(user, parentFolderId, folderName));
    }
  }

  @Test
  public void testFindSubFoldersSuccessfully() {
    User user = new User();
    UUID parentFolderId = UUID.randomUUID();

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(parentFolderId), any(FolderRepository.class)))
          .thenReturn(new Folder());

      List<Folder> subFolders = new ArrayList<>();
      subFolders.add(new Folder());
      System.out.println(2);

      when(folderRepository.getFoldersByParentFolderId(parentFolderId)).thenReturn(subFolders);

      List<FolderDto> result = folderServiceImpl.findSubFolders(user, parentFolderId);

      assertNotNull(result);
      assertEquals(subFolders.size(), result.size());
    }
  }

  @Test
  public void testFindSubFoldersFailureFolderNotFound() {

    User user = new User();
    UUID parentFolderId = UUID.randomUUID();
    String folderName = "NewFolder";

    try (MockedStatic<Utility> utilityClassMock = Mockito.mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(parentFolderId), any(FolderRepository.class)))
          .thenThrow(new NotFoundException("Folder not found with ID: " + parentFolderId));

      assertThrows(
          NotFoundException.class,
          () -> folderServiceImpl.createFolderForUser(user, parentFolderId, folderName));
    }
  }

  @Test
  public void testFindSubFoldersFailureUnauthorizedAccess() {

    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentFolderId = UUID.randomUUID();

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> getFolderByIdOrThrow(eq(parentFolderId), any(FolderRepository.class)))
          .thenReturn(new Folder());

      utilityClassMock
          .when(() -> authorizeUserAccess(any(Folder.class), eq(user.getId()), any(Function.class)))
          .thenThrow(new UnauthorizedException("Unauthorized access!"));

      assertThrows(
          UnauthorizedException.class,
          () -> folderServiceImpl.findSubFolders(user, parentFolderId));
    }
  }

  @Test
  public void testUpdateFolderNameSuccessfully() {

    User user = new User();
    UUID folderId = UUID.randomUUID();
    String newFolderName = "NewFolderName";
    Folder existingFolder = new Folder();

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> Utility.getFolderByIdOrThrow(eq(folderId), any(FolderRepository.class)))
          .thenReturn(existingFolder);

      when(folderRepository.save(any(Folder.class))).thenReturn(existingFolder);
      when(folderMapperImpl.mapTo(any(Folder.class)))
          .thenAnswer(
              invocation -> {
                Folder folderArgument = invocation.getArgument(0);
                FolderDto folderDto = new FolderDto();
                folderDto.setName(folderArgument.getFolderName());
                return folderDto;
              });

      FolderDto updatedFolder = folderServiceImpl.updateFolderName(user, folderId, newFolderName);

      assertNotNull(updatedFolder);
      assertEquals(newFolderName, updatedFolder.getName());
    }
  }

  @Test
  public void testUpdateFolderNameRootFolderRenameAttempt() {
    User user = new User();
    UUID folderId = UUID.randomUUID();
    String newFolderName = "NewFolderName";

    Folder rootFolder = new Folder();
    rootFolder.setRootFolder(true);

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> Utility.getFolderByIdOrThrow(eq(folderId), any(FolderRepository.class)))
          .thenReturn(rootFolder);

      assertThrows(
          FolderUpdateException.class,
          () -> {
            folderServiceImpl.updateFolderName(user, folderId, newFolderName);
          });
    }
  }

  @Test
  public void testUpdateFolderNameFailureNameAlreadyExists() {
    User user = new User();
    user.setId(UUID.randomUUID());

    UUID folderId = UUID.randomUUID();
    UUID parentFolderId = UUID.randomUUID();
    String existingFolderName = "ExistingFolder";
    String newFolderName = "ExistingFolder";
    Folder existingFolder = new Folder();
    existingFolder.setFolderName(existingFolderName);
    existingFolder.setParentFolderId(parentFolderId);
    existingFolder.setId(folderId);

    try (MockedStatic<Utility> utilityClassMock = mockStatic(Utility.class)) {
      utilityClassMock
          .when(() -> Utility.getFolderByIdOrThrow(eq(folderId), any(FolderRepository.class)))
          .thenReturn(existingFolder);

      when(folderRepository.findByUserIdAndParentFolderIdAndFolderName(
              eq(user.getId()), eq(parentFolderId), eq(newFolderName)))
          .thenReturn(Optional.of(existingFolder));

      Throwable exception =
          assertThrows(
              FolderNameNotUniqueException.class,
              () -> {
                folderServiceImpl.updateFolderName(user, folderId, newFolderName);
              });

      assertEquals("Folder name must be unique within the directory.", exception.getMessage());
    }
  }
}
