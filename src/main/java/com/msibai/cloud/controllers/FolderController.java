package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.impl.FolderServiceImpl;
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
}
