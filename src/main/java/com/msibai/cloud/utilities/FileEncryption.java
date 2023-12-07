package com.msibai.cloud.utilities;

import com.msibai.cloud.exceptions.EncryptionException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

/** Utility class for file encryption and decryption operations using AES encryption algorithm. */
public class FileEncryption {

  // Private constructor to prevent instantiation of this utility class
  private FileEncryption() {}

  /**
   * Generates a secret key for encryption/decryption.
   *
   * @param n Key size in bits (e.g., 128, 192, or 256).
   * @return Generated secret key.
   * @throws NoSuchAlgorithmException If the requested cryptographic algorithm is not available.
   */
  public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(n);
    return keyGenerator.generateKey();
  }

  /**
   * Generates an Initialization Vector (IV) for encryption/decryption.
   *
   * @return Generated IV.
   */
  public static IvParameterSpec generateIv() {
    byte[] iv = new byte[16]; // IV length for AES-CBC is 16 bytes
    new SecureRandom().nextBytes(iv);
    return new IvParameterSpec(iv);
  }

  /**
   * Encrypts file content using the specified algorithm, key, and IV.
   *
   * @param algorithm The encryption algorithm (e.g., "AES/CBC/PKCS5Padding").
   * @param key SecretKey used for encryption.
   * @param iv Initialization Vector used for encryption.
   * @param fileContent File content to be encrypted.
   * @return Encrypted file content.
   * @throws EncryptionException If an encryption-related exception occurs.
   */
  public static byte[] encryptFile(
      String algorithm, SecretKey key, IvParameterSpec iv, byte[] fileContent) {
    try {
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);

      return cipher.doFinal(fileContent);
    } catch (NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidAlgorithmParameterException
        | InvalidKeyException
        | BadPaddingException
        | IllegalBlockSizeException ex) {
      throw new EncryptionException("Error during encryption", ex);
    }
  }

  /**
   * Decrypts file content using the specified algorithm, key, and IV.
   *
   * @param algorithm The encryption algorithm (e.g., "AES/CBC/PKCS5Padding").
   * @param key SecretKey used for decryption.
   * @param iv Initialization Vector used for decryption.
   * @param fileContent Encrypted file content to be decrypted.
   * @return Decrypted file content.
   * @throws EncryptionException If a decryption-related exception occurs.
   */
  public static byte[] decryptFile(
      String algorithm, SecretKey key, IvParameterSpec iv, byte[] fileContent) {
    try {
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.DECRYPT_MODE, key, iv);

      return cipher.doFinal(fileContent);
    } catch (NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidAlgorithmParameterException
        | InvalidKeyException
        | BadPaddingException
        | IllegalBlockSizeException ex) {
      throw new EncryptionException("Error during encryption", ex);
    }
  }
}
