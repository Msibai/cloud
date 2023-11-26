package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
