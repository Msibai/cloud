package com.msibai.cloud.controllers;

import com.msibai.cloud.services.AuthenticationService;
import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Controller handling authentication-related endpoints (signup and login). */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  /**
   * Endpoint for user signup.
   *
   * @param signUpDto The SignUpDto containing signup information.
   * @return JwtAuthenticationResponseDto upon successful signup.
   */
  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public JwtAuthenticationResponseDto signUp(@RequestBody @Valid SignUpDto signUpDto) {
    return authenticationService.signUp(signUpDto);
  }

  /**
   * Endpoint for user sign-in.
   *
   * @param signInDto The SignInDto containing login information.
   * @return JwtAuthenticationResponseDto upon successful sign-in.
   */
  @PostMapping("/login")
  public JwtAuthenticationResponseDto signIn(@RequestBody SignInDto signInDto) {
    return authenticationService.signIn(signInDto);
  }
}
