package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.Utility.getUserIdFromToken;
import static com.msibai.cloud.utilities.Utility.tokenIsNotNullOrEmpty;

import com.msibai.cloud.Services.FileService;
import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
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
    tokenIsNotNullOrEmpty(token);
    UUID userId = getUserIdFromToken(token, jwtService);

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
  public FileDto downloadFileFromFolder(String token, UUID folderId, UUID fileId) {

    tokenIsNotNullOrEmpty(token);
    UUID userId = getUserIdFromToken(token, jwtService);
    File file =
        fileRepository
            .findByIdAndFolderId(fileId, folderId)
            .orElseThrow(() -> new NotFoundException("File not found in the folder"));

    if (!file.getUserId().equals(userId)) {

      throw new UnauthorizedException("Unauthorized access, failed to download file!");
    }

    return FileDto.builder()
        .name(file.getName())
        .contentType(file.getContentType())
        .content(file.getContent())
        .size(file.getSize())
        .build();
  }

  @Override
  public void deleteFileFromFolder(String token, UUID folderId, UUID fileId) {
    tokenIsNotNullOrEmpty(token);
    UUID userId = getUserIdFromToken(token, jwtService);
    File file =
            fileRepository
                    .findByIdAndFolderId(fileId, folderId)
                    .orElseThrow(() -> new NotFoundException("File not found in the folder"));

    if (!file.getUserId().equals(userId)) {

      throw new UnauthorizedException("Unauthorized access, failed to download file!");
    }

    fileRepository.delete(file);

  }

  @Override
  public void moveFileToAnotherFolder(String token, UUID folderId, UUID fileId) {}

  private boolean isNameExists(String fileName) {
    return fileRepository.findByName(fileName).isPresent();
  }
}
