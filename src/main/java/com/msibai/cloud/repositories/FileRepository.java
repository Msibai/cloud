package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.File;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, UUID> {

  Optional<File> findByNameAndContentTypeAndFolderId(
      String name, String contentType, UUID folderId);

  Page<File> findByFolderId(UUID folderId, Pageable pageable);
}
