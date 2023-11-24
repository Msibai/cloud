package com.msibai.cloud.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.msibai.cloud.Services.AuthenticationService;
import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignUpDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock private AuthenticationService authenticationService;
  @InjectMocks private AuthenticationController authenticationController;
  private MockMvc mockMvc;

  @Test
  void testSignUp() throws Exception {

    SignUpDto signUpDto =
        new SignUpDto("first", "last", "testuser@test.com", "password", "password");
    JwtAuthenticationResponseDto expectedResponse = new JwtAuthenticationResponseDto("validToken");

    when(authenticationService.signUp(signUpDto)).thenReturn(expectedResponse);

    JwtAuthenticationResponseDto actualResponse = authenticationController.signUp(signUpDto);

    assertEquals(expectedResponse, actualResponse);
  }
}
