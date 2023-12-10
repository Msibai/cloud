package com.msibai.cloud.exceptions;

/**
 * Custom exception class representing an exception thrown when a requested resource is not found.
 */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a NotFoundException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public NotFoundException(String message) {
    super(message);
  }
}
