package com.msibai.cloud.Services.impl;

import static com.msibai.cloud.utilities.FileEncryption.*;
import static com.msibai.cloud.utilities.Utility.*;

import com.msibai.cloud.Services.FileService;
import com.msibai.cloud.dtos.FileDto;
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
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;
  private final FileMapperImpl fileMapperImpl;
  private final FolderRepository folderRepository;

  @Override
  public void uploadFileToFolder(User user, UUID folderId, FileDto fileDto)
      throws FileAlreadyExistsException, NoSuchAlgorithmException {

    validateFileDetails(fileDto);

    Folder folder = getFolderByIdOrThrow(folderId, folderRepository);

    authorizeUserAccess(folder, user.getId(), Folder::getUserId);

    checkIfFileExistsInFolder(fileDto.getName(), fileDto.getContentType(), folderId);

    try {

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

  @Override
  public FileDto downloadFile(User user, UUID fileId) {

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

  @Override
  public void deleteFile(User user, UUID fileId) {

    File file =
        fileRepository
            .findById(fileId)
            .orElseThrow(() -> new NotFoundException("File not found in the folder"));

    authorizeUserAccess(file, user.getId(), File::getUserId);

    fileRepository.delete(file);
  }

  @Override
  public void moveFileToAnotherFolder(
      User user, UUID currentFolderId, UUID fileId, UUID targetFolderId) {

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

  private void validateFileDetails(FileDto file) throws IncompleteFileDetailsException {
    if (file == null
        || file.getName() == null
        || file.getName().isEmpty()
        || file.getContentType() == null
        || file.getContent() == null) {
      throw new IncompleteFileDetailsException("File details are incomplete.");
    }
  }

  private void checkIfFileExistsInFolder(String name, String contentType, UUID folderId)
      throws FileAlreadyExistsException {
    if (fileRepository
        .findByNameAndContentTypeAndFolderId(name, contentType, folderId)
        .isPresent()) {
      throw new FileAlreadyExistsException(
          "File with the same name and type already exists in the folder.");
    }
  }
}
