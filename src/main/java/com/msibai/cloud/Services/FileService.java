package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.dtos.SlimFileDto;
import com.msibai.cloud.entities.User;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for managing files. */
public interface FileService {

  /**
   * Uploads a file to a specified folder.
   *
   * @param user The user performing the upload.
   * @param folderId The ID of the folder where the file will be uploaded.
   * @param file The FileDto object containing details of the file to be uploaded.
   * @throws FileAlreadyExistsException If the file already exists in the folder.
   * @throws NoSuchAlgorithmException If the specified encryption algorithm is not available.
   */
  void uploadFileToFolder(User user, UUID folderId, FileDto file)
      throws FileAlreadyExistsException, NoSuchAlgorithmException;

  /**
   * Finds files in a specified folder with pagination.
   *
   * @param user The user requesting the files.
   * @param folderId The ID of the folder containing the files.
   * @param pageable Pagination information.
   * @return Page of SlimFileDto representing files in the folder.
   */
  Page<SlimFileDto> findByFolderId(User user, UUID folderId, Pageable pageable);

  /**
   * Downloads a file.
   *
   * @param user The user requesting the download.
   * @param fileId The ID of the file to download.
   * @return The FileDto representing the downloaded file.
   */
  FileDto downloadFile(User user, UUID fileId);

  /**
   * Deletes a file.
   *
   * @param user The user requesting the deletion.
   * @param fileId The ID of the file to delete.
   */
  void deleteFile(User user, UUID fileId);

  /**
   * Moves a file to another folder.
   *
   * @param user The user performing the move operation.
   * @param currentFolderId The current folder ID of the file.
   * @param fileId The ID of the file to move.
   * @param targetFolderId The target folder ID where the file will be moved.
   */
  void moveFileToAnotherFolder(User user, UUID currentFolderId, UUID fileId, UUID targetFolderId);
}
