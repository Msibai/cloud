package com.msibai.cloud.utilities;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
import com.msibai.cloud.repositories.FolderRepository;
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

  public static <T> void authorizeUserAccess(
      T object, UUID userId, Function<T, UUID> getUserIdFunction) {
    UUID objectId = getUserIdFunction.apply(object);

    if (!objectId.equals(userId)) {
      throw new UnauthorizedException("Unauthorized access!");
    }
  }

  public static void validateInput(Object input, String fieldName) {
    if (input == null
        || (input instanceof String && ((String) input).trim().isEmpty())
        || (input instanceof UUID && input.toString().isEmpty())) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }

  public static Folder getFolderByIdOrThrow(UUID folderId, FolderRepository folderRepository) {

    return folderRepository
        .findById(folderId)
        .orElseThrow(() -> new NotFoundException("Folder not found with ID: " + folderId));
  }
}
