package com.msibai.cloud.utilities;

import com.msibai.cloud.Services.JwtService;
import com.msibai.cloud.entities.Folder;
import com.msibai.cloud.exceptions.NotFoundException;
import com.msibai.cloud.exceptions.UnauthorizedException;
import com.msibai.cloud.repositories.FolderRepository;
import java.util.UUID;
import java.util.function.Function;

/** Utility class containing common methods used across the application. */
public final class Utility {

  // Private constructor to prevent instantiation of this utility class
  private Utility() {}

  /**
   * Checks if a token is not null or empty.
   *
   * @param token The token to check.
   * @throws IllegalArgumentException If the token is null or empty.
   */
  public static void tokenIsNotNullOrEmpty(String token) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Invalid token");
    }
  }

  /**
   * Retrieves the user ID from a token using JwtService.
   *
   * @param token The token containing user information.
   * @param jwtService The service for JWT operations.
   * @return The UUID representing the user ID.
   * @throws IllegalArgumentException If the user ID in the token is invalid.
   */
  public static UUID getUserIdFromToken(String token, JwtService jwtService) {

    try {
      return UUID.fromString(jwtService.extractUserId(token));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid user ID in the token");
    }
  }

  /**
   * Authorizes user access to an object based on user ID.
   *
   * @param object The object to check access for.
   * @param userId The ID of the user requesting access.
   * @param getUserIdFunction Function to retrieve user ID from the object.
   * @param <T> Type of the object.
   * @throws UnauthorizedException If access is unauthorized.
   */
  public static <T> void authorizeUserAccess(
      T object, UUID userId, Function<T, UUID> getUserIdFunction) {
    UUID objectId = getUserIdFunction.apply(object);

    if (!objectId.equals(userId)) {
      throw new UnauthorizedException("Unauthorized access!");
    }
  }

  /**
   * Validates input by checking if it's null or empty.
   *
   * @param input The input to validate.
   * @param fieldName The name of the field being validated.
   * @throws IllegalArgumentException If the input is null or empty.
   */
  public static void validateInput(Object input, String fieldName) {
    if (input == null
        || (input instanceof String && ((String) input).trim().isEmpty())
        || (input instanceof UUID && input.toString().isEmpty())) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }

  /**
   * Retrieves a Folder entity by its ID from the FolderRepository.
   *
   * @param folderId The ID of the folder to retrieve.
   * @param folderRepository The repository for folder-related operations.
   * @return The Folder entity corresponding to the ID.
   * @throws NotFoundException If the folder is not found.
   */
  public static Folder getFolderByIdOrThrow(UUID folderId, FolderRepository folderRepository) {

    return folderRepository
        .findById(folderId)
        .orElseThrow(() -> new NotFoundException("Folder not found with ID: " + folderId));
  }
}
