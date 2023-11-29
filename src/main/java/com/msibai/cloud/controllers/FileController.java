package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.impl.FileServiceImpl;
import com.msibai.cloud.dtos.FileDto;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class FileController {

  private final FileServiceImpl fileServiceImpl;

  @PostMapping("/folders/{folderId}/upload")
  public ResponseEntity<String> uploadFileToFolder(
      @RequestHeader("Authorization") String token,
      @PathVariable UUID folderId,
      @RequestParam("file") MultipartFile file)
      throws IOException {

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

    fileServiceImpl.uploadFileToFolder(token, folderId, uploadedFile);

    return ResponseEntity.ok(file.getOriginalFilename() + " uploaded successfully!");
  }

  @GetMapping("/folders/{folderId}/files/{fileId}/download")
  public ResponseEntity<byte[]> downloadFileFromFolder(
      @RequestHeader("Authorization") String token,
      @PathVariable UUID folderId,
      @PathVariable UUID fileId) {

    FileDto file = fileServiceImpl.downloadFileFromFolder(token, folderId, fileId);
    byte[] fileData = file.getContent();
    String fileName = file.getName();
    String contentType = file.getContentType();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    headers.setContentType(MediaType.parseMediaType(contentType));

    return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
  }
}
