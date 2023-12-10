package com.msibai.cloud.exceptions;

/** Custom exception class representing file upload-related exceptions. */
public class FileUploadException extends RuntimeException {

  /**
   * Constructs a FileUploadException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the getMessage()
   *     method)
   */
  public FileUploadException(String message) {
    super(message);
  }
}
