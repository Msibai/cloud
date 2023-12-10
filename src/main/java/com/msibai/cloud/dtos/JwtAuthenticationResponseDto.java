package com.msibai.cloud.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO (Data Transfer Object) representing a JWT authentication response. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponseDto {

  /** The JWT token generated upon authentication. */
  private String token;
}
