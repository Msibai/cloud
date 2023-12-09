package com.msibai.cloud.exceptions;

/** Custom exception class representing an exception thrown when a folder name is not unique. */
public class FolderNameNotUniqueException extends RuntimeException {

  /**
   * Constructs a FolderNameNotUniqueException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public FolderNameNotUniqueException(String message) {
    super(message);
  }
}
