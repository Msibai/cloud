package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.File;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, UUID> {

  Optional<File> findByName(String filename);

  Optional<File> findByIdAndFolderId(UUID fileId, UUID folderId);
}
