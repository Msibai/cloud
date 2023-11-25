package com.msibai.cloud.Services;

import com.msibai.cloud.entities.Folder;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface FolderService {
  void createFolderForUser(String folderName, String token);

  Folder findFolderByIdAndUserId(UUID folderId, String token);

  List<Folder> findAllFoldersByUserId(String token);

  void updateFolderByIdAndUserId(UUID folderId, String token);

  void deleteFolderByIdAndUserId(UUID folderId, String token);
}
