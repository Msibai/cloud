package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.User;
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
  public void testFindSubFoldersSuccessfully() {
    User user = new User();
    UUID folderId = UUID.randomUUID();

    List<FolderDto> subFolders = new ArrayList<>();
    subFolders.add(new FolderDto());
    when(folderServiceImpl.findSubFolders(eq(user), eq(folderId))).thenReturn(subFolders);

    ResponseEntity<List<FolderDto>> response = folderController.findSubFolders(user, folderId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(subFolders.size(), Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  public void testUpdateFolderNameSuccessfully() {

    User user = new User();
    UUID folderId = UUID.randomUUID();
    String newFolderName = "NewFolderName";

    doAnswer(
            invocation -> {
              return null;
            })
        .when(folderServiceImpl)
        .updateFolderName(any(User.class), any(UUID.class), any(String.class));

    ResponseEntity<String> response =
        folderController.updateFolderName(user, folderId, newFolderName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Folder name updated successfully", response.getBody());
  }
}
