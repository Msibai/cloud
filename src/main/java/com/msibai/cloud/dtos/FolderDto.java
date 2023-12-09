package com.msibai.cloud.dtos;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO (Data Transfer Object) representing folder information. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderDto {

  /** Unique identifier for the folder. */
  private UUID id;

  /** Name of the folder. */
  private String name;

  /** Date when the folder was created. */
  private LocalDate creationDate;
}
