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

  /**
   * Endpoint to create a new folder within a specified parent folder.
   *
   * @param user The authenticated user creating the folder.
   * @param parentFolderId The ID of the parent folder.
   * @param folderName The name of the new folder.
   * @return ResponseEntity containing the status and message.
   */
  @PostMapping("/{folder-id}/create")
  public ResponseEntity<String> createFolder(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID parentFolderId,
      String folderName) {

    // Check if folderName is empty and return a bad request response
    if (folderName == null || folderName.isEmpty()) {
      return ResponseEntity.badRequest().body("Folder name cannot be empty");
    }

    // Call service method to create a folder
    folderServiceImpl.createFolderForUser(user, parentFolderId, folderName);

    // Return a success response with the folder creation message
    return ResponseEntity.status(HttpStatus.CREATED).body(folderName + " created successfully");
  }

  /**
   * Endpoint to retrieve sub folders of a specified folder.
   *
   * @param user The authenticated user requesting sub folders.
   * @param folderId The ID of the folder to retrieve sub folders.
   * @return ResponseEntity containing the list of FolderDto objects.
   */
  @GetMapping("/{folder-id}")
  public ResponseEntity<List<FolderDto>> findSubFolders(
      @AuthenticationPrincipal User user, @PathVariable("folder-id") UUID folderId) {

    // Call service method to find sub folders for the given folderId
    List<FolderDto> subFolders = folderServiceImpl.findSubFolders(user, folderId);

    // Return the list of sub folders in a success response
    return ResponseEntity.ok(subFolders);
  }

  /**
   * Endpoint to update the name of a folder.
   *
   * @param user The authenticated user updating the folder name.
   * @param folderId The ID of the folder to be updated.
   * @param newFolderName The new name for the folder.
   * @return ResponseEntity containing the status and message.
   */
  @PutMapping("/{folder-id}/update")
  public ResponseEntity<String> updateFolderName(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID folderId,
      @RequestParam("new-folder-name") String newFolderName) {

    // Check if newFolderName is empty and return a bad request response
    if (newFolderName == null || newFolderName.isEmpty()) {
      return ResponseEntity.badRequest().body("Invalid new folder name.");
    }

    // Call service method to update the folder name
    folderServiceImpl.updateFolderName(user, folderId, newFolderName);

    // Return a success response with the update confirmation message
    return ResponseEntity.ok("Folder name updated successfully");
  }
}
