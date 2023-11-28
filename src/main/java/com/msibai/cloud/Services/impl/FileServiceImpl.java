package com.msibai.cloud.Services.impl;

import com.msibai.cloud.Services.FileService;
import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.repositories.FileRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

  private JwtService jwtService;
  private FolderServiceImpl folderServiceImpl;
  private FileRepository fileRepository;

  @Override
  public void uploadFileToFolder(String token, UUID folderId, FileDto file) {

    if (isNameExists(file.getName())) {
      throw new DuplicateKeyException(file.getName() + " is already exists");
    }
    UUID userId;
    try {
      userId = UUID.fromString(jwtService.extractUserId(token));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid user ID in the token");
    }

    folderServiceImpl
        .findFolderByIdAndUserId(folderId, token)
        .orElseThrow(
            () ->
                new NotFoundException("Folder not found or you don't have access to this folder!"));

    File newFile =
        File.builder()
            .name(file.getName())
            .contentType(file.getContentType())
            .content(file.getContent())
            .size(file.getSize())
            .folderId(folderId)
            .userId(userId)
            .build();

    fileRepository.save(newFile);
  }

  @Override
  public File downloadFileFromFolder(String token, UUID folderId, UUID fileId) {

    return null;
  }

  @Override
  public void deleteFileFromFolder(String token, UUID folderId, UUID fileId) {}

  @Override
  public void moveFileToAnotherFolder(String token, UUID folderId, UUID fileId) {}

  private boolean isNameExists(String fileName) {
    return fileRepository.findByName(fileName).isPresent();
  }
}
