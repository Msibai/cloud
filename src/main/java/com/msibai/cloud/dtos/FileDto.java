package com.msibai.cloud.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing file-related information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

  @NotNull(message = "File ID cannot be null")
  private UUID id; // Unique identifier for the file

  @NotBlank(message = "File name cannot be blank")
  @Size(min = 1, max = 255, message = "File name must be between 1 and 255 characters")
  private String name; // Name of the file

  @NotBlank(message = "Content type cannot be blank")
  private String contentType; // MIME type of the file content

  @NotNull(message = "Content cannot be null")
  private byte[] content; // Actual content of the file

  @NotNull(message = "Size cannot be null")
  private Long size; // Size of the file in bytes
}
