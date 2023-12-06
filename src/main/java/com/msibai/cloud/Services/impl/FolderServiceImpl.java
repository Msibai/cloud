package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.Utility.*;

import com.msibai.cloud.Services.FolderService;
import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.*;
import com.msibai.cloud.mappers.impl.FolderMapperImpl;
import com.msibai.cloud.repositories.FolderRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

  private final FolderRepository folderRepository;
  private final JwtService jwtService;
  private final FolderMapperImpl FolderMapperImpl;

  @Override
  public void createRootFolderForNewUser(UUID userId) {

    if (hasExistingRootFolder(userId)) {
      throw new RootFolderAlreadyExistsException("User already has a root directory.");
    }

    Folder rootFolder = createNewFolder("Root", null, userId, true);

    try {
      folderRepository.save(rootFolder);
    } catch (Exception ex) {
      throw new FolderCreationException("Failed to create root folder: " + ex.getMessage());
    }
  }

  @Override
  public void createFolderForUser(User user, UUID parentFolderId, String folderName) {

    validateInput(parentFolderId, "Parent folder ID");
    validateInput(folderName, "Folder name");

    UUID userId = user.getId();

    Folder parentFolder = getFolderByIdOrThrow(parentFolderId, folderRepository);
    authorizeUserAccess(parentFolder, userId, Folder::getUserId);

    validateFolderNameUniqueness(folderRepository, userId, parentFolderId, folderName);

    Folder newFolder = createNewFolder(folderName, parentFolderId, userId, false);

    try {
      folderRepository.save(newFolder);
    } catch (Exception ex) {
      throw new FolderCreationException("Failed to create folder: " + ex.getMessage());
    }
  }

  @Override
  public List<FolderDto> findSubFolders(User user, UUID folderId) {

    validateInput(folderId, "Folder ID");

    Folder parentFolder = getFolderByIdOrThrow(folderId, folderRepository);
    authorizeUserAccess(parentFolder, user.getId(), Folder::getUserId);

    List<Folder> subFolders = folderRepository.getFoldersByParentFolderId(folderId);

    return subFolders.stream().map(FolderMapperImpl::mapTo).collect(Collectors.toList());
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

    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Invalid token");
    }

    UUID userId;
    try {
      userId = UUID.fromString(jwtService.extractUserId(token));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid user ID in the token");
    }

    return folderRepository.findAllByUserId(userId);
  }

  @Override
  public boolean updateFolderByIdAndUserId(UUID folderId, String token, String updatedFolderName) {

    UUID userId = UUID.fromString(jwtService.extractUserId(token));

    Folder existingFolder =
        folderRepository
            .findFolderByIdAndUserId(folderId, userId)
            .orElseThrow(() -> new NotFoundException("Folder not found"));

    if (!existingFolder.getUserId().equals(userId)) {

      throw new UnauthorizedException("Unauthorized access to update folder");
    }

    existingFolder.setFolderName(updatedFolderName);
    folderRepository.save(existingFolder);

    return true;
  }

  @Override
  public void deleteFolderByIdAndUserId(UUID folderId, String token) {}

  private Folder createNewFolder(
      String folderName, UUID parentFolderId, UUID userId, Boolean isRootFolder) {

    return Folder.builder()
        .folderName(folderName)
        .userId(userId)
        .isRootFolder(isRootFolder)
        .creationDate(LocalDate.now())
        .parentFolderId(parentFolderId)
        .build();
  }

  private boolean hasExistingRootFolder(UUID userId) {
    return folderRepository.findByUserIdAndIsRootFolder(userId, true).isPresent();
  }

  private void validateFolderNameUniqueness(
      FolderRepository folderRepository, UUID userId, UUID parentFolderId, String name) {

    Optional<Folder> existingFolderWithSameName =
        folderRepository.findByUserIdAndParentFolderIdAndFolderName(userId, parentFolderId, name);

    if (existingFolderWithSameName.isPresent()) {
      throw new FolderNameNotUniqueException("Folder name must be unique within the directory.");
    }
  }
}
