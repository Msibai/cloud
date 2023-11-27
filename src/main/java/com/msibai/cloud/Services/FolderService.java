package com.msibai.cloud.Services;

import com.msibai.cloud.entities.Folder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface FolderService {
  Folder createFolderForUser(String folderName, String token);

  Optional<Folder> findFolderByIdAndUserId(UUID folderId, String token);

  List<Folder> findAllFoldersByUserId(String token);

  boolean updateFolderByIdAndUserId(UUID folderId, String token, String updatedFolderName);

  void deleteFolderByIdAndUserId(UUID folderId, String token);
}
