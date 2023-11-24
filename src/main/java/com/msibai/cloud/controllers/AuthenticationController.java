package com.msibai.cloud.controllers;

import com.msibai.cloud.Services.AuthenticationService;
import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public JwtAuthenticationResponseDto signUp(@RequestBody @Valid SignUpDto signUpDto) {

    return authenticationService.signUp(signUpDto);
  }

  @PostMapping("/login")
  public JwtAuthenticationResponseDto signIn(@RequestBody SignInDto signInDto) {

    return authenticationService.signIn(signInDto);
  }
}
