package com.msibai.cloud.exceptions;

/** Custom exception class representing an exception thrown when an operation is unauthorized. */
public class UnauthorizedException extends RuntimeException {

  /**
   * Constructs an UnauthorizedException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public UnauthorizedException(String message) {
    super(message);
  }
}
