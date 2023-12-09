package com.msibai.cloud.exceptions;

/** Custom exception class representing encryption-related exceptions. */
public class EncryptionException extends RuntimeException {

  /**
   * Constructs an EncryptionException with the specified detail message and cause.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   * @param cause The cause (which is saved for later retrieval by the getCause() method)
   */
  public EncryptionException(String message, Throwable cause) {
    super(message, cause);
  }
}
