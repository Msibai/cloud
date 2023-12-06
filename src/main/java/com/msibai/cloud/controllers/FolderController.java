package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
import com.msibai.cloud.dtos.FolderDto;
import com.msibai.cloud.entities.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/root")
@RequiredArgsConstructor
public class FolderController {

  private final FolderServiceImpl folderServiceImpl;

  @PostMapping("/{folder-id}/create")
  public ResponseEntity<String> createFolder(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID parentFolderId,
      String folderName) {

    if (folderName == null || folderName.isEmpty()) {
      return ResponseEntity.badRequest().body("Folder name cannot be empty");
    }

    folderServiceImpl.createFolderForUser(user, parentFolderId, folderName);

    return ResponseEntity.status(HttpStatus.CREATED).body(folderName + " created successfully");
  }

  @GetMapping("/{folder-id}")
  public ResponseEntity<List<FolderDto>> findSubFolders(
      @AuthenticationPrincipal User user, @PathVariable("folder-id") UUID folderId) {

    List<FolderDto> subFolders = folderServiceImpl.findSubFolders(user, folderId);

    return ResponseEntity.ok(subFolders);
  }

  @PutMapping("/{folder-id}/update")
  public ResponseEntity<String> updateFolderName(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID folderId,
      @RequestParam("new-folder-name") String newFolderName) {

    if (newFolderName == null || newFolderName.isEmpty()) {
      return ResponseEntity.badRequest().body("Invalid new folder name.");
    }

    folderServiceImpl.updateFolderName(user, folderId, newFolderName);

    return ResponseEntity.ok("Folder name updated successfully");
  }
}
