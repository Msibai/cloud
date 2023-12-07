package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.impl.FileServiceImpl;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.NotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/root")
@RequiredArgsConstructor
public class FileController {

  private final FileServiceImpl fileServiceImpl;

  @PostMapping("/{folder-id}/upload")
  public ResponseEntity<String> uploadFile(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID folderId,
      @RequestParam("file") MultipartFile file)
      throws IOException, NoSuchAlgorithmException {

    if (file == null) {
      return ResponseEntity.badRequest().body("File is missing");
    }

    if (file.getSize() > 2097152000) {
      return ResponseEntity.badRequest().body("File size exceeds the limit");
    }

    FileDto uploadedFile =
        FileDto.builder()
            .name(file.getOriginalFilename())
            .contentType(file.getContentType())
            .content(file.getBytes())
            .size(file.getSize())
            .build();

    fileServiceImpl.uploadFileToFolder(user, folderId, uploadedFile);

    return ResponseEntity.ok(file.getOriginalFilename() + " uploaded successfully!");
  }

  @GetMapping("/{folder-id}/{file-id}/download")
  public ResponseEntity<byte[]> downloadFile(
      @AuthenticationPrincipal User user, @PathVariable("file-id") UUID fileId) {

    FileDto file = fileServiceImpl.downloadFile(user, fileId);
    if (file == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    byte[] fileData = file.getContent();
    String fileName = file.getName();
    String contentType = file.getContentType();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    headers.setContentType(MediaType.parseMediaType(contentType));

    return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
  }

  @DeleteMapping("/{folder-id}/{file-id}/delete")
  public ResponseEntity<String> deleteFile(
      @AuthenticationPrincipal User user, @PathVariable("file-id") UUID fileId) {

    fileServiceImpl.deleteFile(user, fileId);

    return ResponseEntity.ok("File deleted successfully");
  }

  @PutMapping("/folders/{currentFolderId}/files/{fileId}/move")
  public ResponseEntity<String> moveFileToAnotherFolder(
      @RequestHeader("Authorization") String token,
      @PathVariable UUID currentFolderId,
      @PathVariable UUID fileId,
      @RequestParam UUID targetFolderId) {

    try {
      fileServiceImpl.moveFileToAnotherFolder(token, currentFolderId, fileId, targetFolderId);
      return ResponseEntity.ok("File moved successfully to the target folder.");
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while moving the file.");
    }
  }
}
