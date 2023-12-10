package com.msibai.cloud.exceptions;

/** Custom exception class representing folder update-related exceptions. */
public class FolderUpdateException extends RuntimeException {

  /**
   * Constructs a FolderUpdateException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public FolderUpdateException(String message) {
    super(message);
  }
}
