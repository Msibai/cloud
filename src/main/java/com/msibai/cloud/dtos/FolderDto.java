package com.msibai.cloud.dtos;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderDto {

  private  UUID id;
  private String name;
  private LocalDate creationDate;
}
