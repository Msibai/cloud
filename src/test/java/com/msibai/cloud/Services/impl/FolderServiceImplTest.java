package com.msibai.cloud.Services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.helpers.TestHelper;
import com.msibai.cloud.repositories.FolderRepository;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {
  @Mock FolderRepository folderRepository;
  @Mock JwtService jwtService;
  @InjectMocks FolderServiceImpl folderServiceImpl;

  private final TestHelper testHelper = new TestHelper();

  @Test
  void testCreateFolderForUserWithValidInputs() {

    String folderName = "Test Folder";
    String token = "validToken";
    String userId = UUID.randomUUID().toString();

    when(jwtService.extractUserId(token)).thenReturn(userId);

    folderServiceImpl.createFolderForUser(folderName, token);

    verify(jwtService).extractUserId(token);
    verify(folderRepository)
        .save(
            argThat(
                folder ->
                    folder.getFolderName().equals(folderName)
                        && folder.getUserId().equals(UUID.fromString(userId))));
  }

  @Test
  void testCreateFolderForUserWithEmptyName() {

    String token = "validToken";
    String folderName = "";
    String userId = UUID.randomUUID().toString();

    when(jwtService.extractUserId(token)).thenReturn(userId);

    assertThrows(
        IllegalArgumentException.class,
        () -> folderServiceImpl.createFolderForUser(folderName, token));
    verify(jwtService).extractUserId(token);
    verify(folderRepository, never()).save(any());
  }

  @Test
  void testCreateFolderForUserWithTokenWithoutUserId() {

    String token = "tokenWithoutUserId";
    String folderName = "Test Folder";

    when(jwtService.extractUserId(token)).thenReturn("");

    assertThrows(
        IllegalArgumentException.class,
        () -> folderServiceImpl.createFolderForUser(folderName, token));
    verify(jwtService).extractUserId(token);
    verify(folderRepository, never()).save(any());
  }

  @Test
  void testFindFolderByIdAndUserIdAuthorized() {
    UUID folderId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    String token = "validToken";

    when(jwtService.extractUserId(token)).thenReturn(userId.toString());

    Folder expectedFolder = new Folder();
    expectedFolder.setUserId(userId);
    when(folderRepository.findFolderByIdAndUserId(folderId, userId))
        .thenReturn(Optional.of(expectedFolder));

    Optional<Folder> result = folderServiceImpl.findFolderByIdAndUserId(folderId, token);

    assertEquals(Optional.of(expectedFolder), result);
    verify(jwtService).extractUserId(token);
    verify(folderRepository).findFolderByIdAndUserId(folderId, userId);
  }

  @Test
  void testFindFolderByIdAndUserIdFolderNotFoundOrUnauthorized() {
    UUID folderId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    String token = "validToken";

    when(jwtService.extractUserId(token)).thenReturn(userId.toString());
    when(folderRepository.findFolderByIdAndUserId(folderId, userId)).thenReturn(Optional.empty());

    Optional<Folder> result = folderServiceImpl.findFolderByIdAndUserId(folderId, token);

    assertEquals(Optional.empty(), result);
    verify(jwtService).extractUserId(token);
    verify(folderRepository).findFolderByIdAndUserId(folderId, userId);
  }

  @Test
  public void testFindAllFoldersByUserIdSuccess() {
    String validToken = "validToken";
    UUID userId = UUID.randomUUID();
    List<Folder> mockFolders =
        testHelper.createListOfFoldersWithUserId(Arrays.asList("Folder 1", "Folder 2"), userId);

    when(jwtService.extractUserId(validToken)).thenReturn(userId.toString());
    when(folderRepository.findAllByUserId(userId)).thenReturn(mockFolders);

    List<Folder> result = folderServiceImpl.findAllFoldersByUserId(validToken);

    assertEquals(mockFolders, result);
    verify(jwtService).extractUserId(validToken);
    verify(folderRepository).findAllByUserId(userId);
  }

  @Test
  public void testFindAllFoldersByUserIdWithInvalidToken() {
    String invalidToken = "invalidToken";

    when(jwtService.extractUserId(invalidToken))
        .thenThrow(new IllegalArgumentException("Invalid token"));

    try {
      folderServiceImpl.findAllFoldersByUserId(invalidToken);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid user ID in the token", e.getMessage());
      verify(jwtService).extractUserId(invalidToken);
      verifyNoInteractions(folderRepository);
      return;
    }

    fail("Expected an IllegalArgumentException to be thrown");
  }
}
