package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public JwtAuthenticationResponseDto signUp(SignUpDto signUpDto) {

    var user =
        User.builder()
            .firstName(signUpDto.getFirstName())
            .lastName(signUpDto.getLastName())
            .email(signUpDto.getEmail())
            .encodedPassword(passwordEncoder.encode(signUpDto.getPassword()))
            .build();

    user = userService.save(user);
    var jwt = jwtService.generateToken(user);

    return JwtAuthenticationResponseDto.builder().token(jwt).build();
  }
}
