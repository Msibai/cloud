package com.msibai.cloud.exceptions;

/** Custom exception class representing folder creation-related exceptions. */
public class FolderCreationException extends RuntimeException {

  /**
   * Constructs a FolderCreationException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public FolderCreationException(String message) {
    super(message);
  }
}
