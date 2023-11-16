package com.msibai.cloud.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String confirmPassword;
}
