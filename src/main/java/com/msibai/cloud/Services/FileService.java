package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.dtos.SlimFileDto;
import com.msibai.cloud.entities.User;
import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FileService {
  void uploadFileToFolder(User user, UUID folderId, FileDto file)
      throws FileAlreadyExistsException, NoSuchAlgorithmException;

  Page<SlimFileDto> findByFolderId(User user, UUID folderId, Pageable pageable);

  FileDto downloadFile(User user, UUID fileId);

  void deleteFile(User user, UUID fileId);

  void moveFileToAnotherFolder(User user, UUID currentFolderId, UUID fileId, UUID targetFolderId);
}
