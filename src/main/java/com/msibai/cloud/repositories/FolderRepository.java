package com.msibai.cloud.repositories;

import com.msibai.cloud.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {}
