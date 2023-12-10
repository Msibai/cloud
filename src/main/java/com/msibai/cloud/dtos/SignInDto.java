package com.msibai.cloud.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO (Data Transfer Object) representing sign-in credentials. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInDto {

  /** The email address used for sign-in. */
  private String email;

  /** The password associated with the sign-in process. */
  private String password;
}
