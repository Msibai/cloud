package com.msibai.cloud.utilities;

import static org.junit.jupiter.api.Assertions.*;

import com.msibai.cloud.exceptions.EncryptionException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.junit.jupiter.api.Test;

class FileEncryptionTest {

  @Test
  void testEncryptionAndDecryption() {
    try {
      SecretKey key = FileEncryption.generateKey(128);
      IvParameterSpec iv = FileEncryption.generateIv();

      byte[] originalContent = "This is a test file content.".getBytes();

      byte[] encryptedContent =
          FileEncryption.encryptFile("AES/CBC/PKCS5Padding", key, iv, originalContent);

      byte[] decryptedContent =
          FileEncryption.decryptFile("AES/CBC/PKCS5Padding", key, iv, encryptedContent);

      assertArrayEquals(originalContent, decryptedContent);

    } catch (NoSuchAlgorithmException | EncryptionException | NullPointerException e) {
      fail("Exception occurred: " + e.getMessage());
    }
  }

  @Test
  void testEncryptionFailure() {
    byte[] fileContent = "Test content".getBytes();

    assertThrows(
        EncryptionException.class,
        () -> FileEncryption.encryptFile("InvalidAlgorithm", null, null, fileContent));
  }

  @Test
  void testDecryptionFailure() {
    byte[] encryptedContent = "Test content".getBytes();

    assertThrows(
        EncryptionException.class,
        () -> FileEncryption.decryptFile("InvalidAlgorithm", null, null, encryptedContent));
  }
}
