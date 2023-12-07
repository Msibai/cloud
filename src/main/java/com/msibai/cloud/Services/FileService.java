package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.User;

import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface FileService {
  void uploadFileToFolder(User user, UUID folderId, FileDto file)
      throws FileAlreadyExistsException, NoSuchAlgorithmException;

  FileDto downloadFile(User user, UUID fileId);

  void deleteFileFromFolder(String token, UUID folderId, UUID fileId);

  void moveFileToAnotherFolder(
      String token, UUID currentFolderId, UUID fileId, UUID targetFolderId);
}
