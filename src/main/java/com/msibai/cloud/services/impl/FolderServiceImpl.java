package com.msibai.cloud.services.impl;

import static com.msibai.cloud.utilities.Utility.*;

import com.msibai.cloud.services.FolderService;
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

/** Service implementation for managing user folders. */
@AllArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

  private final FolderRepository folderRepository;
  private final FolderMapperImpl folderMapperImpl;

  /**
   * Creates a root folder for a new user if it doesn't exist already.
   *
   * @param userId The ID of the user for whom the root folder is created.
   * @throws RootFolderAlreadyExistsException If the user already has a root folder.
   * @throws FolderCreationException If there's an issue creating the root folder.
   */
  @Override
  public void createRootFolderForNewUser(UUID userId) {

    // Check if the user already has a root folder, throw an exception if exists
    if (hasExistingRootFolder(userId)) {
      throw new RootFolderAlreadyExistsException("User already has a root directory.");
    }

    // Create a new root folder entity
    Folder rootFolder = createNewFolder("Root", null, userId, true);

    try {
      folderRepository.save(rootFolder); // Save the root folder to the repository
    } catch (Exception ex) {
      // Throw an exception if failed to create the root folder
      throw new FolderCreationException("Failed to create root folder: " + ex.getMessage());
    }
  }

  /**
   * Creates a folder for a user inside a parent folder.
   *
   * @param user The user for whom the folder is created.
   * @param parentFolderId The ID of the parent folder.
   * @param folderName The name of the new folder.
   * @throws IllegalArgumentException If parent folder ID or folder name are not valid.
   * @throws NotFoundException If the parent folder is not found.
   * @throws UnauthorizedException If the user is not authorized to access the folder.
   * @throws FolderCreationException If there's an issue creating the folder.
   * @throws FolderNameNotUniqueException If the folder name is not unique within the directory.
   */
  @Override
  public void createFolderForUser(User user, UUID parentFolderId, String folderName) {

    // Validate inputs - parent folder ID and folder name
    validateInput(parentFolderId, "Parent folder ID");
    validateInput(folderName, "Folder name");

    UUID userId = user.getId(); // Get user ID from the user object

    // Get the parent folder by ID or throw an exception if not found
    Folder parentFolder = getFolderByIdOrThrow(parentFolderId, folderRepository);
    authorizeUserAccess(parentFolder, userId, Folder::getUserId); // Authorize user access

    // Check if the folder name is unique within the parent directory
    validateFolderNameUniqueness(folderRepository, userId, parentFolderId, folderName);

    // Create a new folder entity
    Folder newFolder = createNewFolder(folderName, parentFolderId, userId, false);

    try {
      folderRepository.save(newFolder); // Save the new folder to the repository
    } catch (Exception ex) {
      // Throw an exception if failed to create the folder
      throw new FolderCreationException("Failed to create folder: " + ex.getMessage());
    }
  }

  /**
   * Finds sub folders of a given folder for a specific user.
   *
   * @param user The user requesting sub folders.
   * @param folderId The ID of the folder to find sub folders for.
   * @return A list of FolderDto objects representing sub folders.
   * @throws IllegalArgumentException If parent folder ID is not valid.
   * @throws NotFoundException If the folder is not found.
   * @throws UnauthorizedException If the user is not authorized to access the folder.
   */
  @Override
  public List<FolderDto> findSubFolders(User user, UUID folderId) {

    // Validate the folder ID
    validateInput(folderId, "Folder ID");

    // Get the parent folder by ID or throw an exception if not found
    Folder parentFolder = getFolderByIdOrThrow(folderId, folderRepository);
    authorizeUserAccess(parentFolder, user.getId(), Folder::getUserId); // Authorize user access

    // Get the sub folders for the given folder ID from the repository
    List<Folder> subFolders = folderRepository.getFoldersByParentFolderId(folderId);

    // Map the list of Folder entities to FolderDto using FolderMapperImpl
    return subFolders.stream().map(folderMapperImpl::mapTo).collect(Collectors.toList());
  }

  /**
   * Updates the name of a folder for a user.
   *
   * @param user The user updating the folder name.
   * @param folderId The ID of the folder to be updated.
   * @param newFolderName The new name for the folder.
   * @return A FolderDto object representing the updated folder.
   * @throws IllegalArgumentException If parent folder name is not valid.
   * @throws NotFoundException If the folder to update is not found.
   * @throws FolderUpdateException If there's an issue updating the folder name.
   * @throws UnauthorizedException If the user is not authorized to update the folder.
   * @throws FolderNameNotUniqueException If the new folder name is not unique within the directory.
   */
  @Override
  public FolderDto updateFolderName(User user, UUID folderId, String newFolderName) {

    // Validate the new folder name
    validateInput(newFolderName, "Folder name");

    UUID userId = user.getId(); // Get user ID from the user object

    // Get the existing folder by ID or throw an exception if not found
    Folder existingFolder = getFolderByIdOrThrow(folderId, folderRepository);

    // Check if it's the root folder and prevent renaming if it is
    if (existingFolder.isRootFolder()) {
      throw new FolderUpdateException("Cannot update the name of the root directory.");
    }

    authorizeUserAccess(existingFolder, userId, Folder::getUserId); // Authorize user access

    // Check if the new folder name is unique within the directory
    validateFolderNameUniqueness(
        folderRepository, userId, existingFolder.getParentFolderId(), newFolderName);

    existingFolder.setFolderName(newFolderName); // Set the new folder name

    try {
      folderRepository.save(existingFolder); // Save the updated folder to the repository
    } catch (Exception ex) {
      // Throw an exception if failed to update the folder name
      throw new FolderUpdateException("Failed to rename folder: " + ex.getMessage());
    }

    return folderMapperImpl.mapTo(existingFolder); // Return the updated folder DTO
  }

  /**
   * Creates a new folder entity.
   *
   * @param folderName The name of the new folder.
   * @param parentFolderId The ID of the parent folder (can be null for root folders).
   * @param userId The ID of the user to whom the folder belongs.
   * @param isRootFolder Indicates if the folder is a root folder.
   * @return A newly created Folder entity.
   */
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

  /**
   * Checks if the user already has a root folder.
   *
   * @param userId The ID of the user to check.
   * @return {@code true} if the user has a root folder, {@code false} otherwise.
   */
  private boolean hasExistingRootFolder(UUID userId) {
    return folderRepository.findByUserIdAndIsRootFolder(userId, true).isPresent();
  }

  /**
   * Validates the uniqueness of the folder name within a directory.
   *
   * @param folderRepository The repository for folder-related operations.
   * @param userId The ID of the user owning the folder.
   * @param parentFolderId The ID of the parent folder.
   * @param name The name of the folder to check for uniqueness.
   * @throws FolderNameNotUniqueException If the folder name is not unique within the directory.
   */
  private void validateFolderNameUniqueness(
      FolderRepository folderRepository, UUID userId, UUID parentFolderId, String name) {

    // Check if there is an existing folder with the same name in the directory
    Optional<Folder> existingFolderWithSameName =
        folderRepository.findByUserIdAndParentFolderIdAndFolderName(userId, parentFolderId, name);

    // If an existing folder with the same name is found, throw an exception
    if (existingFolderWithSameName.isPresent()) {
      throw new FolderNameNotUniqueException("Folder name must be unique within the directory.");
    }
  }
}
