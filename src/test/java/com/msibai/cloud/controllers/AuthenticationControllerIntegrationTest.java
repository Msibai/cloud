package com.msibai.cloud.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.msibai.cloud.dtos.JwtAuthenticationResponseDto;
import com.msibai.cloud.dtos.SignInDto;
import com.msibai.cloud.dtos.SignUpDto;
import com.msibai.cloud.entities.Role;
import com.msibai.cloud.entities.User;
import com.msibai.cloud.repositories.UserRepository;
import org.junit.jupiter.api.Test;
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
class AuthenticationControllerIntegrationTest {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

  @Autowired TestRestTemplate restTemplate;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;

  @Test
  public void testSignUpEndpoint() {
    SignUpDto signUpDto = new SignUpDto("first", "last", "test@test.com", "password", "password");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<SignUpDto> request = new HttpEntity<>(signUpDto, headers);
    ResponseEntity<JwtAuthenticationResponseDto> responseEntity =
        restTemplate.postForEntity("/api/v1/signup", request, JwtAuthenticationResponseDto.class);
    JwtAuthenticationResponseDto responseDto = responseEntity.getBody();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseDto);
    assertThat(responseDto.getToken()).isNotNull();
    assertThat(responseDto.getToken()).startsWith("eyJ");
  }

  @Test
  public void testSignInEndpoint() {

    String rawPassword = "password";
    String encodedPassword = passwordEncoder.encode(rawPassword);
    User testUser = new User();
    testUser.setFirstName("first");
    testUser.setLastName("last");
    testUser.setEmail("test@test.com");
    testUser.setEncodedPassword(encodedPassword);
    testUser.setRole(Role.ROLE_USER);
    userRepository.save(testUser);

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

    userRepository.deleteAll();
  }
}
