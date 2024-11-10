package com.saudi.tourism.core.services;

/**
 * Service interface for encrypting data.
 */
public interface HyEncryptionService {

  /**
   * Encrypts a given string using the specified encryption key and algorithm.
   *
   * @param stringToEncrypt The string to be encrypted.
   * @return The encrypted string.
   */
  String encrypt(String stringToEncrypt);
}

