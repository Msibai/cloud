package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.Folder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, UUID> {

  Optional<Object> findByUserIdAndIsRootFolder(UUID userId, boolean b);

  Optional<Folder> findFolderByFolderName(String folderName);

  Optional<Folder> findFolderByIdAndUserId(UUID folderId, UUID userId);

  List<Folder> findAllByUserId(UUID userId);

  Optional<Folder> findByUserIdAndParentFolderIdAndFolderName(
      UUID userId, UUID parentFolderId, String name);
}
