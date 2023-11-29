package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.File;
import java.util.UUID;

public interface FileService {
  void uploadFileToFolder(String token, UUID folderId, FileDto file);

  FileDto downloadFileFromFolder(String token, UUID folderId, UUID fileId);

  void deleteFileFromFolder(String token, UUID folderId, UUID fileId);

  void moveFileToAnotherFolder(String token, UUID folderId, UUID fileId);
}
