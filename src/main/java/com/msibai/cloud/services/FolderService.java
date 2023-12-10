package com.msibai.cloud.services;

import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.User;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface FolderService {
  /**
   * Creates a root folder for a new user.
   *
   * @param userId The ID of the user for whom the root folder is created.
   */
  void createRootFolderForNewUser(UUID userId);

  /**
   * Creates a folder for a user inside a parent folder.
   *
   * @param user           The user for whom the folder is created.
   * @param parentFolderId The ID of the parent folder.
   * @param folderName     The name of the new folder.
   */
  void createFolderForUser(User user, UUID parentFolderId, String folderName);

  /**
   * Finds sub folders of a given folder for a specific user.
   *
   * @param user     The user requesting sub folders.
   * @param folderId The ID of the folder to find sub folders for.
   * @return A list of FolderDto objects representing sub folders.
   */
  List<FolderDto> findSubFolders(User user, UUID folderId);

  /**
   * Updates the name of a folder for a user.
   *
   * @param user           The user updating the folder name.
   * @param folderId       The ID of the folder to be updated.
   * @param newFolderName  The new name for the folder.
   * @return A FolderDto object representing the updated folder.
   */
  FolderDto updateFolderName(User user, UUID folderId, String newFolderName);
}
