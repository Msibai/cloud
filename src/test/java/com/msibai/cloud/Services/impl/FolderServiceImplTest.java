package com.msibai.cloud.Services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.repositories.FolderRepository;
import java.util.UUID;
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
}
