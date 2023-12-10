package com.msibai.cloud.exceptions;

/** Custom exception class representing an exception thrown when file details are incomplete. */
public class IncompleteFileDetailsException extends RuntimeException {

  /**
   * Constructs an IncompleteFileDetailsException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public IncompleteFileDetailsException(String message) {
    super(message);
  }
}
