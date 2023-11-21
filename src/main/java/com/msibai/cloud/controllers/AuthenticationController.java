package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.AuthenticationService;
import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/signup")
  public JwtAuthenticationResponseDto signUp(@RequestBody @Valid SignUpDto signUpDto) {

    return authenticationService.signUp(signUpDto);
  }

  @PostMapping("/login")
  public JwtAuthenticationResponseDto signIn(@RequestBody SignInDto signInDto) {

    return authenticationService.signIn(signInDto);
  }
}
