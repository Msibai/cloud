package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.User;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface FolderService {

  void createRootFolderForNewUser(UUID userId);

  void createFolderForUser(User user, UUID parentFolderId, String folderName);

  List<FolderDto> findSubFolders(User user, UUID folderId);

  FolderDto updateFolderName(User user, UUID folderId, String newFolderName);
}
