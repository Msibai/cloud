package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.Folder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

  /**
   * Finds a folder by user ID and whether it is a root folder.
   *
   * @param userId  The ID of the user.
   * @param isRoot  Boolean indicating if it's a root folder.
   * @return An Optional containing the folder object if found, else empty.
   */
  Optional<Object> findByUserIdAndIsRootFolder(UUID userId, boolean isRoot);

  /**
   * Finds a folder by user ID, parent folder ID, and folder name.
   *
   * @param userId         The ID of the user.
   * @param parentFolderId The ID of the parent folder.
   * @param name           The name of the folder.
   * @return An Optional containing the folder object if found, else empty.
   */
  Optional<Folder> findByUserIdAndParentFolderIdAndFolderName(
          UUID userId, UUID parentFolderId, String name);

  /**
   * Retrieves a list of folders by their parent folder ID.
   *
   * @param folderId The ID of the parent folder.
   * @return A list of folders that are sub folders of the provided folder ID.
   */
  List<Folder> getFoldersByParentFolderId(UUID folderId);
}
