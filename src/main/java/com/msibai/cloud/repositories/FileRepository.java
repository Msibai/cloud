package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.File;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository interface for managing File entities. */
public interface FileRepository extends JpaRepository<File, UUID> {

  /**
   * Finds a file by its name, content type, and folder ID.
   *
   * @param name The name of the file.
   * @param contentType The content type of the file.
   * @param folderId The ID of the folder.
   * @return An Optional containing the File if found, otherwise empty.
   */
  Optional<File> findByNameAndContentTypeAndFolderId(
      String name, String contentType, UUID folderId);

  /**
   * Finds files by folder ID with pagination.
   *
   * @param folderId The ID of the folder to search files in.
   * @param pageable Pagination information.
   * @return A Page containing files from the specified folder.
   */
  Page<File> findByFolderId(UUID folderId, Pageable pageable);
}
