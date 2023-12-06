package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.helpers.TestHelper;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FolderControllerTest {

  private final TestHelper testHelper = new TestHelper();
  @Mock FolderServiceImpl folderServiceImpl;
  @InjectMocks FolderController folderController;

  @Test
  void testCreateFolderSuccessfully() {

    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentFolderId = UUID.randomUUID();
    String folderName = "Test folder";

    ResponseEntity<String> response =
        folderController.createFolder(user, parentFolderId, folderName);

    verify(folderServiceImpl, times(1)).createFolderForUser(user, parentFolderId, folderName);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void testCreateFolderWithEmptyFolderName() {
    User user = new User();
    user.setId(UUID.randomUUID());
    UUID parentFolderId = UUID.randomUUID();
    String folderName = "";

    ResponseEntity<String> response =
        folderController.createFolder(user, parentFolderId, folderName);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    verify(folderServiceImpl, never()).createFolderForUser(user, parentFolderId, folderName);
  }

  @Test
  public void testFindFolderById_ValidUUID() {

    UUID folderId = UUID.randomUUID();
    String token = "validToken";
    Folder mockFolder = new Folder();
    mockFolder.setFolderName("TestFolder");

    when(folderServiceImpl.findFolderByIdAndUserId(eq(folderId), eq(token)))
        .thenReturn(Optional.of(mockFolder));

    ResponseEntity<String> response = folderController.findFolderById(token, folderId.toString());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Folder found TestFolder", response.getBody());
  }

  @Test
  public void testFindFolderById_InvalidUUID() {

    String invalidUUID = "invalidUUID";
    String token = "validToken";

    ResponseEntity<String> response = folderController.findFolderById(token, invalidUUID);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid UUID format", response.getBody());
  }

  @Test
  public void testFindFolderById_EmptyFolderId() {

    String emptyFolderId = "";
    String token = "validToken";

    ResponseEntity<String> response = folderController.findFolderById(token, emptyFolderId);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Folder ID cannot be empty", response.getBody());
  }

  @Test
  public void testFindFolderById_FolderNotFound() {

    UUID folderId = UUID.randomUUID();
    String token = "validToken";

    when(folderServiceImpl.findFolderByIdAndUserId(eq(folderId), eq(token)))
        .thenReturn(Optional.empty());

    ResponseEntity<String> response = folderController.findFolderById(token, folderId.toString());

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testGetAllFoldersForUserSuccess() {
    String validToken = "validToken";
    UUID userId = UUID.randomUUID();
    List<Folder> mockFolders =
        testHelper.createListOfFoldersWithUserId(Arrays.asList("Folder 1", "Folder 2"), userId);

    when(folderServiceImpl.findAllFoldersByUserId(validToken)).thenReturn(mockFolders);

    ResponseEntity<List<Folder>> response = folderController.findAllByUserId(validToken);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    verify(folderServiceImpl).findAllFoldersByUserId(validToken);
  }

  @Test
  public void testGetAllFoldersForUserUnauthorized() {
    String invalidToken = "";

    ResponseEntity<List<Folder>> response = folderController.findAllByUserId(invalidToken);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    verifyNoInteractions(folderServiceImpl);
  }

  @Test
  void testUpdateFolderNameSuccessfully() {
    String token = "validToken";
    String folderId = UUID.randomUUID().toString();
    String updatedFolderName = "Updated Folder Name";

    when(folderServiceImpl.updateFolderByIdAndUserId(
            any(UUID.class), eq(token), eq(updatedFolderName)))
        .thenReturn(true);

    ResponseEntity<String> response =
        folderController.updateFolderName(token, folderId, updatedFolderName);

    assertEquals("200 OK", response.getStatusCode().toString());
    assertEquals("Folder name updated successfully", response.getBody());
  }

  @Test
  void testUpdateFolderNameFailure() {
    String token = "validToken";
    String folderId = UUID.randomUUID().toString();
    String updatedFolderName = "Updated Folder Name";

    when(folderServiceImpl.updateFolderByIdAndUserId(
            any(UUID.class), eq(token), eq(updatedFolderName)))
        .thenReturn(false);

    ResponseEntity<String> response =
        folderController.updateFolderName(token, folderId, updatedFolderName);

    assertEquals("500 INTERNAL_SERVER_ERROR", response.getStatusCode().toString());
    assertEquals("Failed to update folder name", response.getBody());
  }
}
