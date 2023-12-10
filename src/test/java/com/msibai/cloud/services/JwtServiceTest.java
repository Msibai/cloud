package com.msibai.cloud.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

  private final UUID uuid = UUID.fromString("e828f529-6c46-4d1d-b99b-bdc3c9839a11");
  private UserDetails userDetails;
  private JwtService jwtService;
  private String token;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    userDetails = new User("testUser", "password", Collections.emptyList());
    token = jwtService.generateToken(userDetails, uuid);
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
  void testExtractUserIdFromToken() {
    var userId = jwtService.extractUserId(token);

    assertThat(uuid).isEqualByComparingTo(UUID.fromString(userId));
  }

  @Test
  void testIsTokenValid() {
    var isTokenValid = jwtService.isTokenValid(token, userDetails);

    assertTrue(isTokenValid);
  }
}
