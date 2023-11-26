package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
import com.msibai.cloud.entities.Folder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class FolderControllerTest {

  @Mock FolderServiceImpl folderServiceImp;
  @InjectMocks FolderController folderController;

  @Test
  void testCreateFolderSuccessfully() {

    String folderName = "Test folder";
    String token = "validToken";

    ResponseEntity<String> response = folderController.createFolder(token, folderName);

    verify(folderServiceImp, times(1)).createFolderForUser(folderName, token);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void testCreateFolderWithEmptyFolderName() {

    String folderName = "";
    String token = "validToken";

    ResponseEntity<String> response = folderController.createFolder(token, folderName);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    verify(folderServiceImp, never()).createFolderForUser(anyString(), anyString());
  }

  @Test
  public void testFindFolderById_ValidUUID() {

    UUID folderId = UUID.randomUUID();
    String token = "validToken";
    Folder mockFolder = new Folder();
    mockFolder.setFolderName("TestFolder");

    when(folderServiceImp.findFolderByIdAndUserId(eq(folderId), eq(token)))
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

    when(folderServiceImp.findFolderByIdAndUserId(eq(folderId), eq(token)))
        .thenReturn(Optional.empty());

    ResponseEntity<String> response = folderController.findFolderById(token, folderId.toString());

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
