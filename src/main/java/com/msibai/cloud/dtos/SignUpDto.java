package com.msibai.cloud.dtos;

import com.msibai.cloud.annotations.PasswordMatches;
import com.msibai.cloud.annotations.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class SignUpDto {
  @NonNull
  @NotEmpty
  private String firstName;
  @NonNull
  @NotEmpty
  private String lastName;
  @NonNull
  @NotEmpty
  @ValidEmail
  private String email;
  @NonNull
  @NotEmpty
  private String password;
  @NonNull
  @NotEmpty
  private String confirmPassword;
}
