package com.msibai.cloud.utilities;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.exceptions.UnauthorizedException;
import java.util.UUID;
import java.util.function.Function;

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

  public static <T> void authorizeUser(T object, UUID userId, Function<T, UUID> getUserIdFunction) {
    UUID objectId = getUserIdFunction.apply(object);

    if (!objectId.equals(userId)) {
      throw new UnauthorizedException("Unauthorized access!");
    }
  }
}
