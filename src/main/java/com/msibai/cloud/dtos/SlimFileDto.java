package com.msibai.cloud.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlimFileDto {
  private UUID id;
  private String name;
  private String contentType;
  private Long size;
}
