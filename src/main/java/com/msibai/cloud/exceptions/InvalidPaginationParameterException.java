package com.msibai.cloud.exceptions;

/**
 * Custom exception class representing an exception thrown when pagination parameters are invalid.
 */
public class InvalidPaginationParameterException extends RuntimeException {

  /**
   * Constructs an InvalidPaginationParameterException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public InvalidPaginationParameterException(String message) {
    super(message);
  }
}
