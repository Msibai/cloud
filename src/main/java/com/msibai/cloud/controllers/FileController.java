package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.impl.FileServiceImpl;
import com.msibai.cloud.dtos.FileDto;
import com.msibai.cloud.dtos.SlimFileDto;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.exceptions.InvalidPaginationParameterException;
import com.msibai.cloud.utilities.PagedResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  @GetMapping("{folder-id}/files")
  public ResponseEntity<PagedResponse<SlimFileDto>> findFilesInFolder(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID folderId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    if (page < 0 || size <= 0) {
      throw new InvalidPaginationParameterException("Invalid pagination parameters.");
    }

    Pageable pageable = PageRequest.of(page, size);
    Page<SlimFileDto> filesPage = fileServiceImpl.findByFolderId(user, folderId, pageable);

    PagedResponse<SlimFileDto> response =
        new PagedResponse<>(
            filesPage.getContent(),
            filesPage.getNumber(),
            filesPage.getSize(),
            filesPage.getTotalElements(),
            filesPage.getTotalPages());

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(filesPage.getTotalElements()));

    return ResponseEntity.ok().headers(headers).body(response);
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

  @PutMapping("/{current-folder-id}/{file-id}/move")
  public ResponseEntity<String> moveFileToAnotherFolder(
      @AuthenticationPrincipal User user,
      @PathVariable("current-folder-id") UUID currentFolderId,
      @PathVariable("file-id") UUID fileId,
      @RequestParam UUID targetFolderId) {

    fileServiceImpl.moveFileToAnotherFolder(user, currentFolderId, fileId, targetFolderId);
    return ResponseEntity.ok("File moved successfully to the target folder.");
  }
}
