package com.saudi.tourism.core.services.impl;


import com.saudi.tourism.core.services.HyEncryptionService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Implementation of the HyEncryptionService.
 */
@Component(service = HyEncryptionService.class,
    immediate = true)
@Slf4j
public class HyEncryptionServiceImpl implements HyEncryptionService {

  /**
   * The constant ALGORITHM.
   */
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
  /**
   * The constant SEPARATOR.
   */
  private static final String SEPARATOR = "::";

  /**
   * key.
   */
  private Key key;
  /**
   * secureRandom.
   */
  private SecureRandom secureRandom;


  /**
   * Saudi Tourism Config.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  @Activate
  protected void activate() {
    try {
      String secretKey = saudiTourismConfigs.getHyEncryptionKey(); // Fetch the secret key using ConfigProvider
      byte[] keyBytes = Base64.getDecoder().decode(secretKey);
      this.key = new SecretKeySpec(keyBytes, "AES");
      this.secureRandom = new SecureRandom(); // Initialize SecureRandom for IV generation
    } catch (Exception e) {
      LOGGER.error("Encryption : Error during activation", e);
    }
  }

  @Override
  public String encrypt(String stringToEncrypt) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM); // Initialize the cipher with AES/CBC/PKCS5Padding
      // Generate a new IV for each encryption
      byte[] iv = new byte[cipher.getBlockSize()];
      secureRandom.nextBytes(iv);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);

      // Initialize the cipher for encryption with the generated IV
      cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
      byte[] encrypted = cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8));

      // Encode the IV and encrypted data to Base64 and concatenate with "::" as a separator
      String ivBase64 = Base64.getEncoder().encodeToString(ivSpec.getIV());
      String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);

      return Base64.getEncoder()
          .encodeToString((encryptedBase64 + SEPARATOR + ivBase64).getBytes(StandardCharsets.UTF_8));

    } catch (Exception e) {
      throw new RuntimeException("Error occurred during encryption", e);
    }
  }

  @Deactivate
  protected void deactivate() {
    this.secureRandom = null;
  }
}
