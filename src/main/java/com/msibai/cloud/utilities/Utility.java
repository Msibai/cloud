package com.msibai.cloud.utilities;

import com.msibai.cloud.Services.JwtService;
import java.util.UUID;

public final class Utility {
  private Utility() {}

  public static void tokenIsNotNullOrEmpty(String token) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Invalid token");
    }
  }

  public static UUID getUserIdFromToken(String token, JwtService jwtService) {

    try {
      return UUID.fromString(jwtService.extractUserId(token));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid user ID in the token");
    }
  }
}
