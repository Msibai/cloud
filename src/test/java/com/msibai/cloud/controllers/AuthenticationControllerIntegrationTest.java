package com.msibai.cloud.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.repositories.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerIntegrationTest {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.1");

  @Autowired TestRestTemplate restTemplate;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;

  @Test
  @Order(1)
  public void testSignUpEndpoint() {
    SignUpDto signUpDto = new SignUpDto("first", "last", "test@test.com", "password", "password");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<SignUpDto> request = new HttpEntity<>(signUpDto, headers);
    ResponseEntity<JwtAuthenticationResponseDto> responseEntity =
        restTemplate.postForEntity("/api/v1/signup", request, JwtAuthenticationResponseDto.class);
    JwtAuthenticationResponseDto responseDto = responseEntity.getBody();

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertNotNull(responseDto);
    assertThat(responseDto.getToken()).isNotNull();
    assertThat(responseDto.getToken()).startsWith("eyJ");
  }

  @Test
  @Order(2)
  public void testSignInEndpoint() {

    SignInDto signInDto = new SignInDto("test@test.com", "password");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<SignInDto> request = new HttpEntity<>(signInDto, headers);
    ResponseEntity<JwtAuthenticationResponseDto> responseEntity =
        restTemplate.postForEntity("/api/v1/login", request, JwtAuthenticationResponseDto.class);

    JwtAuthenticationResponseDto responseDto = responseEntity.getBody();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(responseDto);
    assertThat(responseDto.getToken()).isNotNull();
    assertThat(responseDto.getToken()).startsWith("eyJ");
  }
}
