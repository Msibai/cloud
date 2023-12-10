package com.msibai.cloud.exceptions;

/** Custom exception class representing an exception thrown when the root folder already exists. */
public class RootFolderAlreadyExistsException extends RuntimeException {

  /**
   * Constructs a RootFolderAlreadyExistsException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public RootFolderAlreadyExistsException(String message) {
    super(message);
  }
}
