package com.msibai.cloud.Services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

  private UserDetails userDetails;
  private JwtService jwtService;

  private String token;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    userDetails = new User("testUser", "password", Collections.emptyList());
    token = jwtService.generateToken(userDetails);
  }

  @Test
  void testGenerateToken() {

    assertNotNull(token);
    assertTrue(token.startsWith("eyJ"));
    assertEquals(3, token.split("\\.").length);
  }

  @Test
  void testExtractUsernameFromToken() {
    var username = jwtService.extractUsername(token);

    assertEquals("testUser", username);
  }

  @Test
  void testIsTokenValid() {
    var isTokenValid = jwtService.isTokenValid(token, userDetails);

    assertTrue(isTokenValid);
  }
}
