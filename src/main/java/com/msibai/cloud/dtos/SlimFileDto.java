package com.msibai.cloud.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Data Transfer Object (DTO) representing essential information about a file. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlimFileDto {

  private UUID id; // Unique identifier for the file

  private String name; // Name of the file

  private String contentType; // MIME type of the file content

  private Long size; // Size of the file in bytes
}
