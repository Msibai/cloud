package com.msibai.cloud.dtos;

import com.msibai.cloud.annotations.PasswordMatches;
import com.msibai.cloud.annotations.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class SignUpDto {
  @NotBlank(message = "Name cannot be blank or contain only spaces")
  @NotNull(message = "Name is required, please provide a valid name")
  @Size(min = 3, max = 30, message = "Name length must be between 3 and 30 characters")
  private String firstName;
  @NotBlank(message = "Last Name cannot be blank or contain only spaces")
  @NotNull(message = "Last Name is required, please provide a valid name")
  @Size(min = 3, max = 30, message = "Last length must be between 3 and 30 characters")
  private String lastName;
  @NonNull
  @NotEmpty
  @ValidEmail
  private String email;
  @NotBlank(message = "Last Name cannot be blank or contain only spaces")
  @NotNull(message = "Last Name is required, please provide a valid name")
  @Size(min = 8, max = 30, message = "Invalid Password: Must be of 8 - 30 characters")
  private String password;
  @NotNull(message = "Confirmation password cannot be null")
  @NotEmpty(message = "Confirmation password cannot be empty")
  private String confirmPassword;
}
