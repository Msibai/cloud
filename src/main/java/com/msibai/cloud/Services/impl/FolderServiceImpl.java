package com.msibai.cloud.Services.impl;

import com.msibai.cloud.Services.FolderService;
import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.repositories.FolderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

  private final FolderRepository folderRepository;
  private final JwtService jwtService;

  @Override
  public Folder createFolderForUser(String folderName, String token) {

    if (isFolderExists(folderName)) {
      throw new DuplicateKeyException("Folder with the same name already exist");
    }

    var userId = jwtService.extractUserId(token);

    if (userId == null || userId.isEmpty() || folderName.isEmpty()) {
      throw new IllegalArgumentException("Invalid user ID or folder name");
    }

    Folder folder = new Folder();
    folder.setFolderName(folderName);
    folder.setUserId(UUID.fromString(userId));

    return folderRepository.save(folder);
  }

  @Override
  public Optional<Folder> findFolderByIdAndUserId(UUID folderId, String token) {

    if (folderId == null || token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Invalid folder ID or Invalid token");
    }

    UUID userId;
    try {
      userId = UUID.fromString(jwtService.extractUserId(token));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid user ID in the token");
    }

    Optional<Folder> optionalFolder = folderRepository.findFolderByIdAndUserId(folderId, userId);
    if (optionalFolder.isPresent()) {
      Folder folder = optionalFolder.get();
      UUID folderUserId = folder.getUserId();

      if (!userId.equals(folderUserId)) {
        // Unauthorized access, return empty optional
        return Optional.empty();
      }
    } else {
      // Folder not found for the provided folderId and userId
      return Optional.empty();
    }

    return optionalFolder;
  }

  @Override
  public List<Folder> findAllFoldersByUserId(String token) {

    return null;
  }

  @Override
  public void updateFolderByIdAndUserId(UUID folderId, String token) {}

  @Override
  public void deleteFolderByIdAndUserId(UUID folderId, String token) {}

  private boolean isFolderExists(String folderName) {
    return folderRepository.findFolderByFolderName(folderName).isPresent();
  }
}
