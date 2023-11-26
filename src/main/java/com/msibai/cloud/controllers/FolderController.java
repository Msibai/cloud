package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
import com.msibai.cloud.entities.Folder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FolderServiceImpl folderServiceImpl;

  @PostMapping("/create")
  public ResponseEntity<String> createFolder(
      @RequestHeader("Authorization") String token, String folderName) {

    if (folderName == null || folderName.isEmpty()) {
      return ResponseEntity.badRequest().body("Folder name cannot be empty");
    }

    folderServiceImpl.createFolderForUser(folderName, token);

    return ResponseEntity.status(HttpStatus.CREATED).body(folderName + " created successfully");
  }

  @GetMapping("/{folderId}")
  public ResponseEntity<String> findFolderById(
      @RequestHeader("Authorization") String token, @PathVariable String folderId) {
    if (folderId == null || folderId.isEmpty()) {
      return ResponseEntity.badRequest().body("Folder ID cannot be empty");
    }

    UUID folderUUID;
    try {
      folderUUID = UUID.fromString(folderId);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid UUID format");
    }

    Optional<Folder> optionalFolder = folderServiceImpl.findFolderByIdAndUserId(folderUUID, token);
    return optionalFolder
        .map(folder -> ResponseEntity.ok("Folder found " + folder.getFolderName()))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/all")
  public ResponseEntity<List<Folder>> findAllByUserId(
      @RequestHeader("Authorization") String token) {

    if (token.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    List<Folder> folders = folderServiceImpl.findAllFoldersByUserId(token);

    if (folders.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.ok(folders);
  }
}
