package com.msibai.cloud.controllers;

import com.msibai.cloud.services.impl.FileServiceImpl;
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

  /**
   * Endpoint to upload a file to a specific folder.
   *
   * @param user The authenticated user.
   * @param folderId The ID of the folder where the file will be uploaded.
   * @param file The file to be uploaded.
   * @return ResponseEntity indicating the status of the file upload.
   * @throws IOException If an I/O error occurs.
   * @throws NoSuchAlgorithmException If a requested cryptographic algorithm is not available.
   */
  @PostMapping("/{folder-id}/upload")
  public ResponseEntity<String> uploadFile(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID folderId,
      @RequestParam("file") MultipartFile file)
      throws IOException, NoSuchAlgorithmException {

    // Check if the file is missing
    if (file == null) {
      return ResponseEntity.badRequest().body("File is missing");
    }

    // Check if the file exceeds the size limit
    if (file.getSize() > 209715200) {
      return ResponseEntity.badRequest().body("File size exceeds the limit");
    }

    // Construct FileDto object with file details
    FileDto uploadedFile =
        FileDto.builder()
            .name(file.getOriginalFilename())
            .contentType(file.getContentType())
            .content(file.getBytes())
            .size(file.getSize())
            .build();

    // Call service method to upload the file
    fileServiceImpl.uploadFileToFolder(user, folderId, uploadedFile);

    return ResponseEntity.ok(file.getOriginalFilename() + " uploaded successfully!");
  }

  /**
   * Endpoint to retrieve files in a specific folder with pagination.
   *
   * @param user The authenticated user.
   * @param folderId The ID of the folder to retrieve files from.
   * @param page The page number for pagination (default: 0).
   * @param size The page size for pagination (default: 10).
   * @return ResponseEntity containing PagedResponse with files in the folder.
   * @throws InvalidPaginationParameterException If pagination parameters are invalid.
   */
  @GetMapping("{folder-id}/files")
  public ResponseEntity<PagedResponse<SlimFileDto>> findFilesInFolder(
      @AuthenticationPrincipal User user,
      @PathVariable("folder-id") UUID folderId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    // Validate pagination parameters
    if (page < 0 || size <= 0) {
      throw new InvalidPaginationParameterException("Invalid pagination parameters.");
    }

    Pageable pageable = PageRequest.of(page, size);

    // Retrieve files from the folder using the service method
    Page<SlimFileDto> filesPage = fileServiceImpl.findByFolderId(user, folderId, pageable);

    // Create a PagedResponse and set headers for pagination details
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

  /**
   * Endpoint to download a specific file from a folder.
   *
   * @param user The authenticated user.
   * @param fileId The ID of the file to be downloaded.
   * @return ResponseEntity with the file content for download.
   */
  @GetMapping("/{folder-id}/{file-id}/download")
  public ResponseEntity<byte[]> downloadFile(
      @AuthenticationPrincipal User user, @PathVariable("file-id") UUID fileId) {

    // Retrieve file details using the service method
    FileDto file = fileServiceImpl.downloadFile(user, fileId);
    if (file == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    byte[] fileData = file.getContent();
    String fileName = file.getName();
    String contentType = file.getContentType();

    // Set headers for file download
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    headers.setContentType(MediaType.parseMediaType(contentType));

    return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
  }

  /**
   * Endpoint to delete a specific file from a folder.
   *
   * @param user The authenticated user.
   * @param fileId The ID of the file to be deleted.
   * @return ResponseEntity indicating the status of the file deletion.
   */
  @DeleteMapping("/{folder-id}/{file-id}/delete")
  public ResponseEntity<String> deleteFile(
      @AuthenticationPrincipal User user, @PathVariable("file-id") UUID fileId) {

    // Call service method to delete the file
    fileServiceImpl.deleteFile(user, fileId);

    return ResponseEntity.ok("File deleted successfully");
  }

  /**
   * Endpoint to move a file to another folder.
   *
   * @param user The authenticated user.
   * @param currentFolderId The ID of the current folder containing the file.
   * @param fileId The ID of the file to be moved.
   * @param targetFolderId The ID of the target folder for moving the file.
   * @return ResponseEntity indicating the status of the file move operation.
   */
  @PutMapping("/{current-folder-id}/{file-id}/move")
  public ResponseEntity<String> moveFileToAnotherFolder(
      @AuthenticationPrincipal User user,
      @PathVariable("current-folder-id") UUID currentFolderId,
      @PathVariable("file-id") UUID fileId,
      @RequestParam UUID targetFolderId) {
    // Call service method to move the file to the target folder
    fileServiceImpl.moveFileToAnotherFolder(user, currentFolderId, fileId, targetFolderId);

    return ResponseEntity.ok("File moved successfully to the target folder.");
  }
}
