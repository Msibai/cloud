package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.Utility.*;

import com.msibai.cloud.Services.FolderService;
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
  private final FolderMapperImpl folderMapperImpl;

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

    return subFolders.stream().map(folderMapperImpl::mapTo).collect(Collectors.toList());
  }

  @Override
  public FolderDto updateFolderName(User user, UUID folderId, String newFolderName) {

    validateInput(newFolderName, "Folder name");

    UUID userId = user.getId();

    Folder existingFolder = getFolderByIdOrThrow(folderId, folderRepository);

    if (existingFolder.isRootFolder()) {
      throw new FolderUpdateException("Cannot update the name of the root directory.");
    }

    authorizeUserAccess(existingFolder, userId, Folder::getUserId);

    validateFolderNameUniqueness(
        folderRepository, userId, existingFolder.getParentFolderId(), newFolderName);

    existingFolder.setFolderName(newFolderName);

    try {

      folderRepository.save(existingFolder);
    } catch (Exception ex) {
      throw new FolderUpdateException("Failed to rename folder: " + ex.getMessage());
    }

    return folderMapperImpl.mapTo(existingFolder);
  }

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
