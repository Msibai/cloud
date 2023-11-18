package com.msibai.cloud.Services;

import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.entities.Role;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationResponseDto signUp(SignUpDto signUpDto) {

    var user =
        User.builder()
            .firstName(signUpDto.getFirstName())
            .lastName(signUpDto.getLastName())
            .email(signUpDto.getEmail())
            .encodedPassword(passwordEncoder.encode(signUpDto.getPassword()))
            .role(Role.ROLE_USER)
            .build();

    user = userService.save(user);
    var jwt = jwtService.generateToken(user);

    return JwtAuthenticationResponseDto.builder().token(jwt).build();
  }

  public JwtAuthenticationResponseDto signIn(SignInDto signInDto) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword()));

    var user =
        userRepository
            .findUserByEmail(signInDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

    var jwt = jwtService.generateToken(user);

    return JwtAuthenticationResponseDto.builder().token(jwt).build();
  }
}
