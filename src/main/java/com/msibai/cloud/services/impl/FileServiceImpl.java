package com.msibai.cloud.services.impl;

import static com.msibai.cloud.utilities.FileEncryption.*;
import static com.msibai.cloud.utilities.Utility.*;

import com.msibai.cloud.services.FileService;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.dtos.SlimFileDto;
import com.msibai.cloud.entities.File;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.FileUploadException;
import com.msibai.cloud.exceptions.IncompleteFileDetailsException;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.mappers.impl.FileMapperImpl;
import com.msibai.cloud.repositories.FileRepository;
import com.msibai.cloud.repositories.FolderRepository;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;
  private final FileMapperImpl fileMapperImpl;
  private final FolderRepository folderRepository;

  /**
   * Uploads a file to a specified folder after performing encryption.
   *
   * @param user The user performing the upload.
   * @param folderId The ID of the folder where the file will be uploaded.
   * @param fileDto The FileDto object containing details of the file to be uploaded.
   * @throws FileAlreadyExistsException If the file already exists in the folder.
   * @throws NoSuchAlgorithmException If the specified encryption algorithm is not available.
   */
  @Override
  public void uploadFileToFolder(User user, UUID folderId, FileDto fileDto)
      throws FileAlreadyExistsException, NoSuchAlgorithmException {

    // Validation and authorization checks for the file and folder
    validateFileDetails(fileDto);

    Folder folder = getFolderByIdOrThrow(folderId, folderRepository);

    authorizeUserAccess(folder, user.getId(), Folder::getUserId);

    checkIfFileExistsInFolder(fileDto.getName(), fileDto.getContentType(), folderId);

    try {
      // Encryption of file content and storage in the database
      SecretKey key = generateKey(128);
      String algorithm = "AES/CBC/PKCS5Padding";
      IvParameterSpec ivParameterSpec = generateIv();

      byte[] encryptedContent = encryptFile(algorithm, key, ivParameterSpec, fileDto.getContent());

      File file = fileMapperImpl.mapFrom(fileDto);
      file.setContent(encryptedContent);
      file.setFolderId(folderId);
      file.setUserId(user.getId());
      file.setEncryptionKey(Base64.getEncoder().encodeToString(key.getEncoded()));
      file.setIv(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()));

      fileRepository.save(file);

    } catch (FileUploadException ex) {
      throw new FileUploadException("Failed to upload file: " + ex.getMessage());
    }
  }

  /**
   * Retrieves files in a specified folder with user authorization and pagination.
   *
   * @param user The user requesting the files.
   * @param folderId The ID of the folder containing the files.
   * @param pageable Pagination information.
   * @return Page of SlimFileDto representing files in the folder.
   */
  @Override
  public Page<SlimFileDto> findByFolderId(User user, UUID folderId, Pageable pageable) {

    // Retrieval of files from the folder with authorization and mapping to SlimFileDto
    Page<File> fileList = fileRepository.findByFolderId(folderId, pageable);
    fileList.forEach(file -> authorizeUserAccess(file, user.getId(), File::getUserId));

    return fileList.map(fileMapperImpl::mapToSlim);
  }

  /**
   * Downloads a file with decryption if authorized for the user.
   *
   * @param user The user requesting the download.
   * @param fileId The ID of the file to download.
   * @return The FileDto representing the downloaded file.
   */
  @Override
  public FileDto downloadFile(User user, UUID fileId) {

    // File retrieval and decryption before returning the FileDto
    File file =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new NotFoundException("File not found in the folder"));

    authorizeUserAccess(file, user.getId(), File::getUserId);

    String keyStr = file.getEncryptionKey();
    String ivStr = file.getIv();

    byte[] keyBytes = Base64.getDecoder().decode(keyStr);
    byte[] ivBytes = Base64.getDecoder().decode(ivStr);

    byte[] decryptedContent =
        decryptFile(
            "AES/CBC/PKCS5Padding",
            new SecretKeySpec(keyBytes, "AES"),
            new IvParameterSpec(ivBytes),
            file.getContent());

    FileDto fileDto = fileMapperImpl.mapTo(file);
    fileDto.setContent(decryptedContent);

    return fileDto;
  }

  /**
   * Deletes a file from the folder if authorized.
   *
   * @param user The user requesting the deletion.
   * @param fileId The ID of the file to delete.
   */
  @Override
  public void deleteFile(User user, UUID fileId) {

    File file =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new NotFoundException("File not found in the folder"));

    authorizeUserAccess(file, user.getId(), File::getUserId);

    fileRepository.delete(file);
  }

  /**
   * Moves a file to another folder if authorized.
   *
   * @param user The user performing the move operation.
   * @param currentFolderId The current folder ID of the file.
   * @param fileId The ID of the file to move.
   * @param targetFolderId The target folder ID where the file will be moved.
   */
  @Override
  public void moveFileToAnotherFolder(
      User user, UUID currentFolderId, UUID fileId, UUID targetFolderId) {

    // Moving the file to another folder if authorized
    File fileToMove =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new NotFoundException("File not found in the folder"));

    authorizeUserAccess(fileToMove, user.getId(), File::getUserId);

    Folder targetFolder = getFolderByIdOrThrow(targetFolderId, folderRepository);
    authorizeUserAccess(targetFolder, user.getId(), Folder::getUserId);

    fileToMove.setFolderId(targetFolderId);
    fileRepository.save(fileToMove);
  }

  /**
   * Validates if the FileDto contains complete file details.
   *
   * @param file The FileDto to be validated.
   * @throws IncompleteFileDetailsException If file details are incomplete.
   */
  private void validateFileDetails(FileDto file) throws IncompleteFileDetailsException {

    // Check if the FileDto contains all necessary details (name, content, etc.)
    if (file == null
        || file.getName() == null
        || file.getName().isEmpty()
        || file.getContentType() == null
        || file.getContent() == null) {

      // Throw an exception if any details are missing
      throw new IncompleteFileDetailsException("File details are incomplete.");
    }
  }

  /**
   * Checks if a file with the same name and type already exists in the folder.
   *
   * @param name The name of the file.
   * @param contentType The content type of the file.
   * @param folderId The ID of the folder to check for file existence.
   * @throws FileAlreadyExistsException If a file with the same name and type already exists.
   */
  private void checkIfFileExistsInFolder(String name, String contentType, UUID folderId)
      throws FileAlreadyExistsException {

    // Check if a file with the same name and type already exists in the folder
    if (fileRepository
        .findByNameAndContentTypeAndFolderId(name, contentType, folderId)
        .isPresent()) {

      // Throw an exception if a similar file is found
      throw new FileAlreadyExistsException(
          "File with the same name and type already exists in the folder.");
    }
  }
}
