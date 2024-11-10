package com.saudi.tourism.core.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PKCEUtil {
  private PKCEUtil() {
  }

  /**
   * The constant ALPHA_NUMERIC_STRING.
   */
  private static final String ALPHA_NUMERIC_STRING =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
  /**
   * The Secure random.
   */
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  /**
   * The constant CODE_VERIFIER_LENGTH.
   */
  private static final int CODE_VERIFIER_LENGTH = 128;


  public static String generateCodeVerifier() {
    StringBuilder builder = new StringBuilder(CODE_VERIFIER_LENGTH);
    for (int i = 0; i < CODE_VERIFIER_LENGTH; i++) {
      int character = (int) (SECURE_RANDOM.nextDouble() * ALPHA_NUMERIC_STRING.length());
      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    return builder.toString();
  }

  public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
    byte[] bytes = codeVerifier.getBytes();
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(bytes, 0, bytes.length);
    byte[] digest = md.digest();
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
  }
}
